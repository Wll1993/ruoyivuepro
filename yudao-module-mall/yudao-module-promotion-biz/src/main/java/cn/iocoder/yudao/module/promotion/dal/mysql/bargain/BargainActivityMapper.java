package cn.iocoder.yudao.module.promotion.dal.mysql.bargain;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.promotion.controller.admin.bargain.vo.BargainActivityPageReqVO;
import cn.iocoder.yudao.module.promotion.dal.dataobject.bargain.BargainActivityDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 砍价活动 Mapper
 *
 * @author HUIHUI
 */
@Mapper
public interface BargainActivityMapper extends BaseMapperX<BargainActivityDO> {

    default PageResult<BargainActivityDO> selectPage(BargainActivityPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BargainActivityDO>()
                .likeIfPresent(BargainActivityDO::getName, reqVO.getName())
                .eqIfPresent(BargainActivityDO::getStatus, reqVO.getStatus())
                .orderByDesc(BargainActivityDO::getId));
    }

    default List<BargainActivityDO> selectListByStatus(Integer status) {
        return selectList(BargainActivityDO::getStatus, status);
    }

    /**
     * 更新活动库存
     *
     * @param id    活动编号
     * @param count 扣减的库存数量
     * @return 影响的行数
     */
    default int updateActivityStock(Long id, int count) {
        return update(null, new LambdaUpdateWrapper<BargainActivityDO>()
                .eq(BargainActivityDO::getId, id)
                .gt(BargainActivityDO::getStock, 0) // TODO @puhui999：不是 > 0，是要大于 count 哈
                .setSql("stock = stock - " + count));
    }

}
