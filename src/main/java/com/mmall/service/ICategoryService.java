package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.service
 * @date 2019/6/11 20:36
 */
public interface ICategoryService {
    ServerResponse<String> addCategory(String categoryName,Integer parentId);

    ServerResponse<String> updateCategoryName(Integer categoryId,String categoryName);

    ServerResponse<List<Category>> getCategory(Integer categoryId);

    ServerResponse selectCategoryAndChildrenById(Integer categoryId);
}
