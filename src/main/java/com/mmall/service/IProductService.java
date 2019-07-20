package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.service
 * @date 2019/6/12 19:36
 */
public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNum,int pageSize,Integer categoryId,String orderBy);

    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,Integer pageNum,Integer pageSize);

    ServerResponse<Product> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductListManage(int pageNum,int pageSize);
}
