package com.thinkgem.jeesite.modules.crm.api;


import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.api.model.RespModel;
import com.thinkgem.jeesite.modules.crm.client.entity.Client;
import com.thinkgem.jeesite.modules.crm.client.entity.MyClient;
import com.thinkgem.jeesite.modules.crm.client.service.ClientService;
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
@RequestMapping(value = "${apiPath}/notice")
public class NoticeRestController extends BaseController {

    public static final class Constants {
        // 根据用户Id得到通知列表
        public static final String getByUserId = "/getByUserId";
        // 如果列表中的信息不全，就需要获得通知详情接口
        public static final String getById = "/getById";
        //
        public static final String c = "";
        //
        public static final String d = "";

    }

    @Autowired
    ClientService clientService;

    // Retrieve All Client
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<Client>> listAllClients() {
        List<Client>  clients = clientService.findList(new Client());

        if (clients.isEmpty()) {
            // You may decide to return HttpStatus.NOT_FOUND
            return new ResponseEntity<List<Client>>(HttpStatus.NO_CONTENT);
        }
        RespModel<List<Client>> respModel = new RespModel<List<Client>>("0");
        respModel.setData(clients);
        return new ResponseEntity<List<Client>>(clients, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<RespModel<List<Client>>> listClients() {

        Page<Client> page = new Page<Client>(1, 10);

        page = clientService.findPage(page, new Client());

        List<Client>  clients = page.getList();

        RespModel<List<Client>> respModel = new RespModel<List<Client>>("0");
        if (clients.isEmpty()) {
            // You may decide to return HttpStatus.NOT_FOUND
            respModel.setCode("-1");
            return new ResponseEntity<RespModel<List<Client>>>(HttpStatus.NO_CONTENT);
        }
        respModel.setData(clients);
        return new ResponseEntity<RespModel<List<Client>>>(respModel, HttpStatus.OK);
    }

    // Retrieve Single Client
    @RequestMapping(value = "/getClient", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RespModel<Client>> getClient(@RequestBody MyClient client) {
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
