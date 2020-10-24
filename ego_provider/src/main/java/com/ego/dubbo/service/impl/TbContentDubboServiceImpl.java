package com.ego.dubbo.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.dubbo.service.TbContentDubboService;
import com.ego.mapper.TbContentMapper;
import com.ego.pojo.TbContent;
import com.ego.pojo.TbContentExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TbContentDubboServiceImpl implements TbContentDubboService {
    @Autowired
    private TbContentMapper tbContentMapper;
    @Override
    public List<TbContent> selectBypage(Long categoryId, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber,pageSize);
        TbContentExample example = new TbContentExample();
        if(categoryId!=0){
            example.createCriteria().andCategoryIdEqualTo(categoryId);
        }
        List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);
        PageInfo<TbContent> pi = new PageInfo<>(list);
        return pi.getList();
    }

    @Override
    public long selectCountByCategoryid(Long categoryId) {
        TbContentExample example = new TbContentExample();
        if(categoryId!=0){
            example.createCriteria().andCategoryIdEqualTo(categoryId);
        }
        return tbContentMapper.countByExample(example);
    }

    @Override
    public int insert(TbContent tbContent) {
        return tbContentMapper.insert(tbContent);
    }

    @Override
    public int update(TbContent tbContent) {
        return tbContentMapper.updateByPrimaryKeySelective(tbContent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(long[] ids) throws DaoException {
        int index = 0 ;
        for(long id : ids){
            index += tbContentMapper.deleteByPrimaryKey(id);
        }
        if(index==ids.length){
            return 1;
        }
        throw new DaoException("删除失败");
    }

    @Override
    public List<TbContent> selectByCategoryId(Long id) {
        TbContentExample example = new TbContentExample();
        example.createCriteria().andCategoryIdEqualTo(id);
        example.setOrderByClause("updated desc");
        return tbContentMapper.selectByExampleWithBLOBs(example);

    }

    @Override
    public TbContent selectById(Long categoryId) {
        return tbContentMapper.selectByPrimaryKey(categoryId);
    }
}
