package com.liuyanzhao.ssm.blog.interceptor;

import com.liuyanzhao.ssm.blog.entity.*;

import com.liuyanzhao.ssm.blog.enums.ArticleStatus;
import com.liuyanzhao.ssm.blog.enums.LinkStatus;

import com.liuyanzhao.ssm.blog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyanzhao
 */
@Component
public class HomeResourceInterceptor implements HandlerInterceptor {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private OptionsService optionsService;

    @Autowired
    private MenuService menuService;



    /**
     * 在请求处理之前执行，该方法主要是用于准备资源数据的，然后可以把它们当做请求属性放到WebRequest中
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object contoller) throws IOException {
        boolean b = contoller instanceof HandlerMethod;
        if (!b) {
            return true;
        }

        HandlerMethod currentController = (HandlerMethod) contoller;
        if (currentController == null) {
            return false;
        }
        String controllerName = currentController.getMethod().getName();

        // 菜单显示
        List<Menu> menuList = menuService.listMenu();
        request.setAttribute("menuList", menuList);

        List<Category> categoryList = categoryService.listCategory();
        request.setAttribute("allCategoryList", categoryList);
        if (controllerName.equals("index")) {
            //获得网站概况
            List<String> siteBasicStatistics = new ArrayList<String>();
            siteBasicStatistics.add(articleService.countArticle(ArticleStatus.PUBLISH.getValue()) + "");
            siteBasicStatistics.add(articleService.countArticleComment() + "");
            siteBasicStatistics.add(categoryService.countCategory() + "");
            siteBasicStatistics.add(tagService.countTag() + "");
            siteBasicStatistics.add(linkService.countLink(LinkStatus.NORMAL.getValue()) + "");
            siteBasicStatistics.add(articleService.countArticleView() + "");

            request.setAttribute("siteBasicStatistics", siteBasicStatistics);
        }

        //最后更新的文章
        Article lastUpdateArticle = articleService.getLastUpdateArticle();
        request.setAttribute("lastUpdateArticle", lastUpdateArticle);

        //页脚显示
        //博客基本信息显示(Options)
        Options options = optionsService.getOptions();
        request.setAttribute("options", options);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

    }
}