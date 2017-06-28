package com.thinkgem.jeesite.modules.crm.api;


import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.api.model.RespModel;
import com.thinkgem.jeesite.modules.crm.client.entity.Client;
import com.thinkgem.jeesite.modules.crm.client.entity.MyClient;
import com.thinkgem.jeesite.modules.crm.client.service.ClientService;
import com.thinkgem.jeesite.modules.crm.common.UserUtils2;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.api.model.RespModel;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.crm.client.entity.Client;
import com.thinkgem.jeesite.modules.crm.client.entity.MyClient;
import com.thinkgem.jeesite.modules.crm.client.service.ClientService;
import com.thinkgem.jeesite.modules.customer.entity.Customer;
import com.thinkgem.jeesite.modules.customer.service.CustomerService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Created by rgz on 28/04/2017.
 */
@RestController
@RequestMapping(value = "${apiPath}/dept")
public class DeptRestController extends BaseController {

    public static final class Constants {
        // 根据userId得到他所负责的部门
        public static final String getByUserId = "/getByUserId";
        // 根据部门Id得到部门详情
        public static final String getById = "/getById";
        // 得到所有的部门
        public static final String getDepts = "/getDepts";
        public static final String d = "";

    }

    @Autowired
    ClientService clientService;

    @Autowired
    OfficeService officeService;

    // Retrieve All Client
    @RequestMapping(value = Constants.getDepts, method = RequestMethod.GET)
    public ResponseEntity<RespModel<List<Office>>> getDepts(String json) {

        logger.info(Constants.getDepts + ": " + json);

        List<Office> officeList = UserUtils.getOfficeList();

        UserUtils2.getOfficeList("rengangzai");

        RespModel<List<Office>> respModel = new RespModel<List<Office>>("0");
        respModel.setData(officeList);

        return new ResponseEntity<RespModel<List<Office>>>(respModel, HttpStatus.OK);
    }

    @RequestMapping(value = Constants.getById, method = RequestMethod.GET)
    public ResponseEntity<RespModel<Office>> getById(String json) {

        Office office = new Office();
        office.setName("销售部");
        office = officeService.getByName(office);

        Page<Client> page = new Page<Client>(1, 10);

        page = clientService.findPage(page, new Client());

        List<Client>  clients = page.getList();

        RespModel<List<Client>> respModel = new RespModel<List<Client>>("0");
        if (clients.isEmpty()) {
            // You may decide to return HttpStatus.NOT_FOUND
            respModel.setCode("-1");
            // return new ResponseEntity<RespModel<List<Client>>>(HttpStatus.NO_CONTENT);
        }
        respModel.setData(clients);
        // return new ResponseEntity<RespModel<List<Client>>>(respModel, HttpStatus.OK);
        return null;
    }

    // Retrieve Single Client
    @RequestMapping(value = Constants.getByUserId, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RespModel<Client>> getByUserId(@RequestBody MyClient client) {
        // System.out.println("Fetching Client with id " + id);
        Client fullClient = clientService.get(client.getId());

        RespModel<Client> respModel = new RespModel<Client>("0");

        if (fullClient == null) {
            // System.out.println("Client with id " + id + " not found");
            respModel.setCode("-1");
            respModel.setMsg("没有找到客户信息。");
            return new ResponseEntity<RespModel<Client>>(HttpStatus.NOT_FOUND);
        }
        respModel.setData(fullClient);
        return new ResponseEntity<RespModel<Client>>(respModel, HttpStatus.OK);
    }

    // Create a Client
    @RequestMapping(value = "/saveClient", method = RequestMethod.POST)
    public ResponseEntity<Void> createClient(@RequestBody Client client, UriComponentsBuilder ucBuilder) {
        System.out.println("Creating Client " + client.getCustomerName());

        if (clientService.get(client.getId()) != null) {
            System.out.println("A Client with name " + client.getCustomerName() + " already exist");
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }
        clientService.save(client);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/{id}").buildAndExpand(client.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    // Update a Client
    // @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    // public ResponseEntity<Client> updateClient(@PathVariable("id") String id, @RequestBody Client client) {
    //     System.out.println("Updating Client " + id);
    //
    //     Client currentClient = clientService.get(id);
    //
    //     if (currentClient == null) {
    //         System.out.println("Client with id " + id + " not found");
    //         return new ResponseEntity<Client>(HttpStatus.NOT_FOUND);
    //     }
    //
    //     currentClient.setCustomerName(client.getCustomerName());
    //
    //     clientService.save(currentClient);
    //     return new ResponseEntity<Client>(currentClient, HttpStatus.OK);
    // }

    // Delete a Client



    // Update a Client
    @RequestMapping(value = "/saveContact")
    public ResponseEntity<RespModel<Client>> saveContact(@RequestBody Client client) {
        // System.out.println("Updating Client " + id);
        RespModel<Client> respModel = new RespModel<Client>("0");

        Client currentClient = clientService.get(client.getId());
        if (currentClient == null) {
            System.out.println("Client with id " + client.getId() + " not found");
            respModel.setCode("-1");
            respModel.setMsg("查不到此客户信息");
            return new ResponseEntity<RespModel<Client>>(HttpStatus.NOT_FOUND);
        }
        currentClient.setCustomerContactList(client.getCustomerContactList());
        clientService.save(currentClient);

        return new ResponseEntity<RespModel<Client>>(respModel, HttpStatus.OK);
    }

}
