package com.ego.dubbo.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.mapper.TbItemDescMapper;
import com.ego.mapper.TbItemMapper;
import com.ego.mapper.TbItemParamItemMapper;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbItemDesc;
import com.ego.pojo.TbItemParamItem;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

// 这个注解表示把当前类实现的接口发布到zookeeper中
@Service
public class TbItemDubboServiceImpl implements TbItemDubboService {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbItemDescMapper tbItemDescMapper;
    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;
    @Override
    public List<TbItem> selectByPage(int pageSize, int pageNumber) {
        // 分页插件要写在查询上面。否则插件无效。一般都写在第一行
        PageHelper.startPage(pageNumber,pageSize);
        // Example相当于sql中where,没有where时参数为null即可
        // select * from tb_item limit ?,?
        List<TbItem> list = tbItemMapper.selectByExample(null);
        // 千万不要忘记构造方法参数。
        PageInfo<TbItem> pi = new PageInfo<>(list);
        return pi.getList();
    }

    @Override
    public long selectCount() {
        return tbItemMapper.countByExample(null);
    }

    @Override
    // 监听到异常，执行事务回滚。声明式事务注解
    @Transactional
    public int updateStatusByIds(long[] ids, int status) throws DaoException {
        int index = 0 ;
        Date date = new Date();
        for(long id : ids){
            TbItem tbItem = new TbItem();
            tbItem.setId(id);
            tbItem.setStatus((byte)status);
            tbItem.setUpdated(date);
            index+=tbItemMapper.updateByPrimaryKeySelective(tbItem);
        }
        // 修改的条数和数组长度一样，说明都修改了。成功成功
        if(index==ids.length){
            return 1;
        }
        // 让事务回滚
        throw new DaoException("批量修改失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(TbItem tbItem, TbItemDesc tbItemDesc, TbItemParamItem tbItemParamItem) throws DaoException {
        int index = tbItemMapper.insert(tbItem);
        if(index==1){
            int index2 = tbItemDescMapper.insert(tbItemDesc);
            if(index2==1){
                int index3 = tbItemParamItemMapper.insert(tbItemParamItem);
                if(index3==1){
                    return 1;
                }
            }
        }
        throw new DaoException("新增失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(TbItem tbItem, TbItemDesc tbItemDesc, TbItemParamItem tbItemParamItem) throws DaoException {
        // 一定要调用selective，动态sql
        int index = tbItemMapper.updateByPrimaryKeySelective(tbItem);
        if(index==1){
            int index2 = tbItemDescMapper.updateByPrimaryKeySelective(tbItemDesc);
            if(index2==1){
                int index3 = tbItemParamItemMapper.updateByPrimaryKeySelective(tbItemParamItem);
                if(index3==1){
                    return 1;
                }
            }
        }
        throw  new DaoException("修改失败");
    }

    @Override
    public TbItem selectById(Long id) {
        return tbItemMapper.selectByPrimaryKey(id);
    }
}
