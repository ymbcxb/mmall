package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.controller.portal
 * @date 2019/6/22 20:36
 */
@RestController
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    /**
     * 产品详情
     * @param productId
     * @return
     */
    @RequestMapping("detail")
    public ServerResponse<Product> detail(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iProductService.getProductDetail(productId);
    }

    /**
     * 产品搜索及动态排序List
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list")
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "price_asc") String orderBy,
                                         @RequestParam(value = "categoryId")Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iProductService.getProductList(pageNum,pageSize,categoryId,orderBy);
    }
}
