package cn.iocoder.yudao.module.product.service.spu;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.product.controller.admin.spu.vo.*;
import cn.iocoder.yudao.module.product.dal.dataobject.spu.SpuDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 商品spu Service 接口
 *
 * @author 芋道源码
 */
public interface SpuService {

    /**
     * 创建商品spu
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createSpu(@Valid SpuCreateReqVO createReqVO);

    /**
     * 更新商品spu
     *
     * @param updateReqVO 更新信息
     */
    void updateSpu(@Valid SpuUpdateReqVO updateReqVO);

    /**
     * 删除商品spu
     *
     * @param id 编号
     */
    void deleteSpu(Integer id);

    /**
     * 获得商品spu
     *
     * @param id 编号
     * @return 商品spu
     */
    SpuDO getSpu(Integer id);

    /**
     * 获得商品spu列表
     *
     * @param ids 编号
     * @return 商品spu列表
     */
    List<SpuDO> getSpuList(Collection<Integer> ids);

    /**
     * 获得商品spu分页
     *
     * @param pageReqVO 分页查询
     * @return 商品spu分页
     */
    PageResult<SpuDO> getSpuPage(SpuPageReqVO pageReqVO);

    /**
     * 获得商品spu列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 商品spu列表
     */
    List<SpuDO> getSpuList(SpuExportReqVO exportReqVO);

}
