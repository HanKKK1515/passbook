package com.hll.passbook.service.impl;

import com.hll.passbook.constant.Constants;
import com.hll.passbook.dao.MerchantsDao;
import com.hll.passbook.entity.Merchants;
import com.hll.passbook.hbase.HBaseService;
import com.hll.passbook.service.IInventoryService;
import com.hll.passbook.service.IUserPassService;
import com.hll.passbook.utils.RowKeyGenUtils;
import com.hll.passbook.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h1>获取优惠券库存信息，只返回用户没有领取的</h1>
 */
@Slf4j
@Service
public class InventoryServiceImpl implements IInventoryService {
    /** HBase 工具类 */
    private final HBaseService hBaseService;
    /** MerchantsDao 接口 */
    private final MerchantsDao merchantsDao;
    /** UserPassService 接口 */
    private final IUserPassService userPassService;

    @Autowired
    public InventoryServiceImpl(HBaseService hBaseService, MerchantsDao merchantsDao, IUserPassService userPassService) {
        this.hBaseService = hBaseService;
        this.merchantsDao = merchantsDao;
        this.userPassService = userPassService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response getInventoryInfo(Long userId) throws Exception {
        Response allPassInof = userPassService.getUserAllPassInof(userId);

        List<PassInfo> passInfos = (List<PassInfo>) allPassInof.getData();
        List<PassTemplate> passTemplates = passInfos.stream().map(PassInfo::getPassTemplate).collect(Collectors.toList());

        List<String> excludeIds = new ArrayList<>();
        passTemplates.forEach(passTemplate -> excludeIds.add(RowKeyGenUtils.genPassTemplateRowKey(passTemplate)));
        List<PassTemplate> availablePassTemplate = getAvailablePassTemplate(excludeIds);
        List<PassTemplateInfo> passTemplateInfos = builderPassTemplateInfo(availablePassTemplate);

        InventoryResponse inventoryResponse = new InventoryResponse(userId, passTemplateInfos);
        return new Response(inventoryResponse);
    }

    /**
     * <h2>获取系统中可用的优惠券</h2>
     * @param excludeIds 用户已经领取的优惠券 id
     * @return {@link PassTemplate}
     */
    private List<PassTemplate> getAvailablePassTemplate(List<String> excludeIds) {
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);
        filterList.addFilter(
                new SingleColumnValueFilter(
                        Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                        Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                        CompareFilter.CompareOp.GREATER,
                        Bytes.toBytes("0")
                )
        );
        filterList.addFilter(
                new SingleColumnValueFilter(
                        Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                        Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                        CompareFilter.CompareOp.EQUAL,
                        Bytes.toBytes("-1")
                )
        );

        List<PassTemplate> passTemplates = hBaseService.searchAllByFilter(Constants.PassTemplateTable.TABLE_NAME, filterList, PassTemplate.class);

        List<PassTemplate> availablePassTemplate = new ArrayList<>();
        for (PassTemplate passTemplate : passTemplates) {
            if (excludeIds.contains(RowKeyGenUtils.genPassTemplateRowKey(passTemplate))) {
                continue;
            }
            Date cur = new Date();
            if (cur.getTime() <= passTemplate.getEnd().getTime() && cur.getTime() >= passTemplate.getStart().getTime()) {
                availablePassTemplate.add(passTemplate);
            }
        }

        return availablePassTemplate;
    }

    /**
     * <h2>构造优惠券信息 </h2>
     * @param passTemplates {@link PassTemplate}
     * @return {@link PassTemplateInfo}
     */
    private List<PassTemplateInfo> builderPassTemplateInfo(List<PassTemplate> passTemplates) {
        Stream<Integer> streamIds = passTemplates.stream().map(PassTemplate::getId);
        List<Integer> ids = streamIds.collect(Collectors.toList());
        List<Merchants> merchantses = merchantsDao.findAllByIdIn(ids);
        Map<Integer, Merchants> merchantsMap = new HashMap<>();
        merchantses.forEach(merchants -> merchantsMap.put(merchants.getId(), merchants));

        List<PassTemplateInfo> passTemplateInfos = new ArrayList<>();
        for (PassTemplate passTemplate : passTemplates) {
            Merchants merchants = merchantsMap.getOrDefault(passTemplate.getId(), null);
            if (null == merchants) {
                log.error("Merchants Error: {}", passTemplate.getId());
                continue;
            }
            passTemplateInfos.add(new PassTemplateInfo(passTemplate, merchants));
        }
        return passTemplateInfos;
    }
}
