package com.example.libbook.controller.user;

import com.example.libbook.entity.Product;
import com.example.libbook.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    ProductService bookService;


//    @GetMapping("")
//    @ResponseBody
//    public List<Product> getAllProduct() {
//        ModelAndView mv = new ModelAndView();
//    }
}
