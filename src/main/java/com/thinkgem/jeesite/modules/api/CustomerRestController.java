package com.thinkgem.jeesite.modules.api;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.modules.api.model.RespModel;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.customer.entity.Customer;
import com.thinkgem.jeesite.modules.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by rgz on 28/04/2017.
 */
// @RestController
// @RequestMapping(value = "${apiPath}/customer")
public class CustomerRestController {

    @Autowired
    ProjectApplyExternalService externalService;

    @Autowired
    CustomerService customerService;

    // Retrieve All Client
    @RequestMapping(value = "/customer/", method = RequestMethod.GET)
    public ResponseEntity<List<Customer>> listAllCustomers() {
        List<Customer>  customers = customerService.findList(new Customer());

        if (customers.isEmpty()) {
            // You may decide to return HttpStatus.NOT_FOUND
            return new ResponseEntity<List<Customer>>(HttpStatus.NO_CONTENT);
        }
        RespModel<List<Customer>> respModel = new RespModel<List<Customer>>("0");
        respModel.setData(customers);
        return new ResponseEntity<List<Customer>>(customers, HttpStatus.OK);
    }

    @RequestMapping(value = "/customerCode/", method = RequestMethod.GET)
    public ResponseEntity<RespModel<List<Customer>>> listAllCustomers1() {

        Page<Customer> page = new Page<Customer>(1, 10);

        page = customerService.findPage(page, new Customer());

        List<Customer>  customers = page.getList();

        RespModel<List<Customer>> respModel = new RespModel<List<Customer>>("0");
        if (customers.isEmpty()) {
            // You may decide to return HttpStatus.NOT_FOUND
            respModel.setCode("-1");
            return new ResponseEntity<RespModel<List<Customer>>>(HttpStatus.NO_CONTENT);
        }
        respModel.setData(customers);
        return new ResponseEntity<RespModel<List<Customer>>>(respModel, HttpStatus.OK);
    }

    // Retrieve Single Client
    @RequestMapping(value = "/customer/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RespModel<Customer>> getCustomer(@PathVariable("id") String id) {
        System.out.println("Fetching Client with id " + id);
        Customer customer = customerService.get(id);

        RespModel<Customer> respModel = new RespModel<Customer>("0");

        if (customer == null) {
            System.out.println("Client with id " + id + " not found");
            return new ResponseEntity<RespModel<Customer>>(HttpStatus.NOT_FOUND);
        }
        respModel.setData(customer);
        return new ResponseEntity<RespModel<Customer>>(respModel, HttpStatus.OK);
    }

    // Create a Client
    @RequestMapping(value = "/customer/", method = RequestMethod.POST)
    public ResponseEntity<Void> createCustomer(@RequestBody Customer customer, UriComponentsBuilder ucBuilder) {
        System.out.println("Creating Client " + customer.getCustomerName());

        if (customerService.get(customer.getId()) != null) {
            System.out.println("A Client with name " + customer.getCustomerName() + " already exist");
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }
        customerService.save(customer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/customer/{id}").buildAndExpand(customer.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    // Update a Client
    @RequestMapping(value = "/customer/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Customer> updateCustomer(@PathVariable("id") long id, @RequestBody Customer customer) {
        System.out.println("Updating Client " + id);

        Customer currentCustomer = customerService.get(String.valueOf(id));

        if (currentCustomer == null) {
            System.out.println("Client with id " + id + " not found");
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }

        currentCustomer.setCustomerName(customer.getCustomerName());

        customerService.save(currentCustomer);
        return new ResponseEntity<Customer>(currentCustomer, HttpStatus.OK);
    }

    // Delete a Client

    // 上传图片

    /**
     * springmvc.xml:
     * <!-- 文件上传,id必须设置为multipartResolver -->
     *     <bean id="multipartResolver"
     *           class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
     *         <!-- 设置文件上传大小 -->
     *         <property name="maxUploadSize" value="5000000" />
     *     </bean>
     *
     * jsp页面：
     * <form action="/product/upload" method="post" enctype="multipart/form-data">
     *     <input type="file" name="imgFile">
     *     <button type="submit">提交</button>
     * </form>
     * @param imgFile
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("/upload")
    public String upload(MultipartFile imgFile, HttpServletRequest request) throws IOException {
        // 获取文件名
        String fileName = imgFile.getOriginalFilename();
        // 获取图片后缀
        String extName = fileName.substring(fileName.lastIndexOf("."));
        if(extName.equals(".jpg")){
            // 项目在容器中实际发布运行的根路径
            String realPath=request.getSession().getServletContext().getRealPath("/");

            // 自定义的文件名称
            String trueFileName = System.currentTimeMillis() + fileName;
            // 设置存放图片文件的路径
            String path=realPath+"/upload/" +trueFileName;
            // 开始上传
            imgFile.transferTo(new File(path));
        }

        return "list";
    }


}
