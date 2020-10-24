package com.ego.search.service.impl;

import com.ego.dubbo.service.TbItemCatDubboService;
import com.ego.dubbo.service.TbItemDescDubboService;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbItemCat;
import com.ego.pojo.TbItemDesc;
import com.ego.search.pojo.SearchPojo;
import com.ego.search.service.SearchService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    // Spring boot 2.0 此处有一个bug
    private SolrTemplate solrOperations;
    @Reference
    private TbItemDubboService tbItemDubboService;
    @Reference
    private TbItemCatDubboService tbItemCatDubboService;
    @Reference
    private TbItemDescDubboService tbItemDescDubboService;
    @Override
    public Map<String, Object> search(String q,int page,int size) {

        // 设置查询条件
        Criteria c = new Criteria("item_keywords");
        c.is(q);

        // 相当于可视化界面query菜单项左侧全部内容
        HighlightQuery query = new SimpleHighlightQuery(c);

        // 设置排序规则
        query.addSort(Sort.by(Sort.Direction.DESC,"_version_"));

        // 设置分页条件
        query.setOffset((long)size*(page-1));
        query.setRows(size);

        // 高亮条件
        HighlightOptions hlOptions = new HighlightOptions();
        hlOptions.setSimplePrefix("<span style='color:red'>");
        hlOptions.setSimplePostfix("</span>");
        hlOptions.addField("item_title item_sell_point");
        query.setHighlightOptions(hlOptions);

        // 相当于可视化界面点击查询按钮得出的总体值
        HighlightPage<SearchPojo> hlPage = solrOperations.queryForHighlightPage("ego", query, SearchPojo.class);
        // 取出包含高亮的数据
        List<HighlightEntry<SearchPojo>> highlighted = hlPage.getHighlighted();

        // 创建一个集合，集合放所有查询到的数据，对应页面中
        List<SearchPojo> listResult = new ArrayList<>();

        for(HighlightEntry<SearchPojo> hlEntity : highlighted){
            // 非高亮数据，相当于可视化界面中docs
            SearchPojo searchPojo = hlEntity.getEntity();
            // 把从solr中取出的image转换为images
            String image = searchPojo.getImage();
            searchPojo.setImages(image!=null&&!image.equals("")?image.split(","):new String[1]);
            // 高亮数据，相当于可视化界面中highlighting
            List<HighlightEntry.Highlight> listHL = hlEntity.getHighlights();
            for(HighlightEntry.Highlight highlight : listHL){
                // 当前这个对象item_title包含高亮数据
                if(highlight.getField().getName().equals("item_title")){
                    // 替换普通数据
                    searchPojo.setTitle(highlight.getSnipplets().get(0));
                }
            }
            // 把处理完的包含高亮数据的对象放入到集合中
            listResult.add(searchPojo);
        }

        Map<String,Object> map = new HashMap<>();
        map.put("itemList",listResult);
        map.put("query",q);
        map.put("totalPages",hlPage.getTotalPages());
        map.put("page",page);
        return map;
    }

    @Override
    public int insert(long []  ids) {
        List<SearchPojo> list = new ArrayList<>();
        for(Long id : ids) {
            SearchPojo sp = new SearchPojo();
            TbItem tbItem = tbItemDubboService.selectById(id);
            sp.setImage(tbItem.getImage());
            sp.setTitle(tbItem.getTitle());
            sp.setId(id);
            sp.setPrice(tbItem.getPrice());
            sp.setSellPoint(tbItem.getSellPoint());
            TbItemCat tbItemCat = tbItemCatDubboService.selectByid(tbItem.getCid());
            sp.setCatName(tbItemCat.getName());
            TbItemDesc tbItemDesc = tbItemDescDubboService.selectById(id);
            sp.setDesc(tbItemDesc.getItemDesc());
            list.add(sp);
        }
        UpdateResponse response = solrOperations.saveBeans("ego", list);
        solrOperations.commit("ego");
        if(response.getStatus()==0){
            return 1;
        }
        return 0;
    }

    @Override
    public int delete(String [] ids) {
        List<String> list = Arrays.asList(ids);
        UpdateResponse response = solrOperations.deleteByIds("ego", list);
        solrOperations.commit("ego");
        if(response.getStatus()==0){
            return 1;
        }
        return 0;
    }

}
