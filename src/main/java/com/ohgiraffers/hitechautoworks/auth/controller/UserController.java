package com.ohgiraffers.hitechautoworks.auth.controller;


import com.ohgiraffers.hitechautoworks.auth.dto.*;

import com.ohgiraffers.hitechautoworks.auth.service.Details.AuthUserInfo;
import com.ohgiraffers.hitechautoworks.auth.service.UserService;
import com.ohgiraffers.hitechautoworks.part.dto.PartDTO;
import com.ohgiraffers.hitechautoworks.part.service.PartService;
import com.ohgiraffers.hitechautoworks.repair.dto.RepairDTO;
import com.ohgiraffers.hitechautoworks.res.dto.DeleteCommentDTO;
import com.ohgiraffers.hitechautoworks.res.dto.EditCommentDTO;
import com.ohgiraffers.hitechautoworks.res.dto.ResCommentDTO;
import com.ohgiraffers.hitechautoworks.res.dto.ResDTO;
import com.ohgiraffers.hitechautoworks.res.service.ResService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

@Controller
@SessionAttributes("userDTO")
public class UserController {

    private AuthUserInfo authUserInfo;

    @Autowired
    private UserService userService;

    @Autowired
    private ResService resService;

    @Autowired
    private PartService partService;


    @ModelAttribute
    public void addUserToModel(Model model){
        AuthUserInfo authUserInfo = new AuthUserInfo();
        UserDTO userDTO = authUserInfo.getUserDTO();
        int userCode = userDTO.getUserCode();
        UserDTO userDTO1 = userService.findUserCode(userCode);
        model.addAttribute("userDTO",userDTO1);
    }


    @GetMapping("/user/dashboard")
    public void dashboard(Model model) {
        String userName = ((UserDTO) model.getAttribute("userDTO")).getUserName();
        model.addAttribute("userName", userName);
    }

    @GetMapping("/customer/dashboard")
    public void dashboard2(Model model) {
        String userName = ((UserDTO) model.getAttribute("userDTO")).getUserName();
        model.addAttribute("userName", userName);
    }

    @GetMapping("/employee/dashboard")
    public void employee(Model model) {
        String userName = ((UserDTO) model.getAttribute("userDTO")).getUserName();
        model.addAttribute("userName", userName);
    }


    @GetMapping("/customer/account/AccountModify")
    public void AccountModify(Model model, HttpSession session) {
        authUserInfo = new AuthUserInfo();
        UserDTO userDTO = authUserInfo.getUserDTO();
        UserRegistDTO registDTO = userService.getAll(userDTO.getUserCode());
        System.out.println("registDTO = " + registDTO);
        model.addAttribute("userRegistDTO", registDTO);
        String message = (String) session.getAttribute("errorMessage");
        model.addAttribute("errorMessage", message);
        session.removeAttribute("errorMessage");
    }


    @PostMapping("/customer/account/deleteUser")
    public String deleteUser() {
        authUserInfo = new AuthUserInfo();
        UserDTO userDTO = authUserInfo.getUserDTO();
        int userCode = userDTO.getUserCode();
        userService.deletePeople(userCode);

        return "customer/res/res";

    }

    @GetMapping("/user/mainpage")
    public void mainpage(Model model) {

    }

    // 메인페이지 부품 수량 Js
    @PostMapping("/user/getpartchart")
    @ResponseBody
    public ChartResponseDTO getpartchart() {
        ChartResponseDTO chart = userService.getpartchart();

        return chart;
    }

    // 유저 통계
    @PostMapping("/user/getPersonChart")
    @ResponseBody
    public Map<String,Integer> getPersonChart() {
        Map<String,Integer> getPersonChart = userService.getpersonchart();

        return getPersonChart;
    }

    @GetMapping("/user/mypage")
    public void mypage(Model model) {

    }


    @PostMapping(value = "/user/mypage/changepass", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public int changepass(@RequestBody PasswordRequestDTO request, Model model) {
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        String pw = ((UserDTO) model.getAttribute("userDTO")).getPassword();
        int userCode = ((UserDTO) model.getAttribute("userDTO")).getUserCode();
        int result = userService.changepass(currentPassword, newPassword, pw, userCode);

        return result;
    }



    @GetMapping("/user/common")
    public String common(Model model) {
        List<ResDTO> resList = resService.findAllres();
        model.addAttribute("resList", resList);

        return "user/common";
    }


    @PostMapping("/user/res/ressearch")
    public String resgo(Model model,@RequestParam String resCode, @RequestParam String resName){

        if(resCode != "" && resName == ""){
            int resCodeInt = Integer.parseInt(resCode);
            ResDTO resList = resService.findUserRes(resCodeInt);
            model.addAttribute("resList", resList);
        } else if (resCode == "" && resName != ""){
            List<ResDTO> resList = resService.findNameRes(resName);
            model.addAttribute("resList", resList);
        } else {
            List<ResDTO> resList = resService.findAllres();
            model.addAttribute("resList", resList);
        }

        return "user/common";
    }

    @GetMapping("/user/partAllCall")
    public String partAllCall(Model model) {

        List<PartDTO> partList = partService.selectAllPart();
        model.addAttribute("partList", partList);

        return "user/partAllCall";
    }

    @PostMapping("/user/partAllCall")
    public String partAllCall2(@RequestParam String partName, @RequestParam String partCode,
                               Model model) {

        if (partName == "" && partCode == "") {
            List<PartDTO> partList = partService.selectAllPart();
            model.addAttribute("partList", partList);
            System.out.println("partList = " + partList);
        } else if (partName == "") {
            List<PartDTO> partList = partService.selectPartByCode(Integer.parseInt(partCode));
            System.out.println("partList = " + partList);
            model.addAttribute("partList", partList);
        } else {
            List<PartDTO> partList = partService.partSearchBtPartName(partName);
            System.out.println("partList = " + partList);
            model.addAttribute("partList", partList);
        }

        return "user/partAllCall";
    }

    @PostMapping("/user/registpart")
    public String registpart(@RequestParam Map<String, String> parts, Model model){

        int result = partService.addPart(parts);
        if(result == 1) {
            String partName = parts.get("partName");
            model.addAttribute("result", result);
            model.addAttribute("partName", partName);
        }

        return "redirect:/user/partAllCall";
    }


    @PostMapping("/user/mypage/update")
    public String updateUser(@RequestParam Map<String, String> myprofile) {

        AuthUserInfo authUserInfo = new AuthUserInfo();
        UserDTO userDTO = authUserInfo.getUserDTO();
        int userCode = userDTO.getUserCode(); // 기준으로

        myprofile.put("userCode", String.valueOf(userCode));
        userService.updateUser(myprofile);

        return "redirect:/user/mypage";
    }

    @GetMapping("/user/partAdd")
    public void partAdd(Model model) {
    }

    @GetMapping("/user/testPage")
    public void testpage(Model model, @RequestParam int resCode, HttpSession session) {

        ResDTO res = resService.findUserRes(resCode / 123456);// 들어올때 resCode 123456 나눠줘야댐 (나중에 제대로 암호화 ㄱㄱ)
        model.addAttribute("res", res);
        String date = String.valueOf(res.getDate());
        String repair = resService.findStatus(resCode/123456);
        if (repair == null){
            repair = "대기";
        }
        model.addAttribute("repair", repair);
        model.addAttribute("sqldate", date.substring(0,19));
        List<ResCommentDTO> resCommentDTO = resService.findComment(resCode / 123456);
        model.addAttribute("resComment", resCommentDTO);
        List<Map<String,Object>> replyComment = resService.replyComment(resCode / 123456);
        model.addAttribute("replyComment",replyComment);
        if (session.getAttribute("result") != null) {
            model.addAttribute("result", session.getAttribute("result"));
            session.removeAttribute("result");
        }

    }

    @PostMapping("/user/registcomment")
    public String testPage2(@RequestParam String comment, @RequestParam int resCode,Model model) {

        int userCode = ((UserDTO) model.getAttribute("userDTO")).getUserCode();
        resService.registcomment(comment,resCode,userCode);
        return "redirect:/user/testPage?resCode=" + 123456 * resCode;
    }

    @PostMapping("/user/resUpdate")
    public String resModify(@RequestParam int resCode ,@RequestParam String fixOption,@RequestParam String date,@RequestParam String extra, Model model,
                            HttpSession session){

        int result = resService.resModify(resCode,fixOption,date,extra);
        if (result == 1){
            session.setAttribute("result",result);
        }
        return "redirect:/user/testPage?resCode=" + 123456 * resCode;
    }

    @PostMapping("/user/resDelete")
    public String resDelete(@RequestParam int resCode){
        resService.resDelete(resCode);

        return "user/testPage";
    }


    @GetMapping("/user/rescustomer")
    public void resccustomer(Model model) {

        int userCode = ((UserDTO) model.getAttribute("userDTO")).getUserCode();
        List<ResDTO> resList = resService.findCustomerRes(userCode);
        model.addAttribute("resList",resList);
    }


    @PostMapping("/user/reseditComment")
    @ResponseBody
    public int editComment(@RequestBody EditCommentDTO editCommentDTO){
        int resReplyCode = editCommentDTO.getResReplyCode();
        String editcomment = editCommentDTO.getStr();
        int rescode = editCommentDTO.getRescode();

        int result = resService.updateComment(resReplyCode, editcomment);

        return result;
    }

    @PostMapping("/user/reseditComment1")
    @ResponseBody
    public int editComment1(@RequestBody Map<String,Object> info){

        int result = resService.updateReComment(info);
        System.out.println("info = " + info);


        return result;
    }



    @GetMapping("/user/customermypage")
    public String customermypage(Model model, @RequestParam int customerUserCode, HttpSession session){

        int customerCode = customerUserCode / 123456 ;
        UserDTO userDTO = userService.findUserCode(customerCode);
        model.addAttribute("customerDTO",userDTO);
        session.setAttribute("customerUserCode",customerUserCode);
        return "user/anotherProfile";
    }

    @PostMapping("/user/customermypage/update")
    public String updateAnotherUser(@RequestParam Map<String, String> myprofile, HttpSession session) {


        int customerUserCode = (int) session.getAttribute("customerUserCode");
        if (customerUserCode != 0) {
            myprofile.put("userCode", String.valueOf(customerUserCode / 123456));
            userService.updateUser(myprofile);
            session.removeAttribute("customerUserCode");
        }
        return "redirect:/user/customermypage?customerUserCode=" + customerUserCode;
    }

    @PostMapping("/user/deleteComment")
    @ResponseBody
    public int deleteComment(@RequestBody DeleteCommentDTO deleteCommentDTO){
        int resReplyCode = deleteCommentDTO.getResReplyCode();
        int rescode = deleteCommentDTO.getRescode();
        int result = resService.deleteComment(resReplyCode);

        return result;
    }

    @PostMapping("/user/deleteComment1")
    @ResponseBody
    public int deleteComment1(@RequestBody Map<String,Object> deleteInfo){

        int result = resService.deleteReComment(deleteInfo);

        return result;
    }

    @GetMapping("/user/res")
    public String res() {

        return "user/res";
    }

    @GetMapping("/user/selectRes")
    public String selectRes() {

        return "user/selectRes";
    }

    @PostMapping("/user/res/resTime")
    @ResponseBody
    public Map<String, Object> checkResTime(@RequestBody Map<String,Date> date) {

        System.out.println("fadsfasdfasdfas" + date.get("date"));
        Date date1 = date.get("date");
        List<String> time = userService.getTime(date1);
        // "disabledTimes": ["9", "11", "14"] 이대로 받으면 이거 비활성화 무조건 배열로 !!!!!!!!!

        System.out.println("time = " + time);
        Map<String,Object> disabledTimes = new HashMap();
//        List<String> disabledTimesList = Arrays.asList("9", "10", "11");

        disabledTimes.put("disabledTimes",time);


        return disabledTimes;
    }

    @PostMapping("/user/res/Submit")
    @ResponseBody
    public String resSubmit(@RequestBody Map<String,Object> info,Model model){
        String date = (String) info.get("date");
        String time = (String) info.get("selectedRadioValue");
        String dateTime = date+' '+time;
        System.out.println("dateTime = " + dateTime);
        for(String key : info.keySet()) {
            String value = (String) info.get(key);
            System.out.println(key + " : " + value);
        }
        String option = (String) info.get("message");
        String resExtra = (String) info.get("resExtra");
        int userCode = ((UserDTO) model.getAttribute("userDTO")).getUserCode();
        resService.insertRes(userCode,option,dateTime,resExtra);

        return "1";
    }


    // 작업중
    @GetMapping("/user/resCar")
    public String resCar() {

        return "user/resCar";
    }

    @PostMapping("/user/res/carSubmit")
    public String carSubmit(@RequestParam String inputMessage) {

        System.out.println("inputMessage = " + inputMessage);

        return "user/res";
    }

    @PostMapping("/user/getCalendar")
    @ResponseBody
    public List<Map<String, Object>> getCalendar(Model model) {

        int userCode = ((UserDTO) model.getAttribute("userDTO")).getUserCode();
        List<Map<String, Object>> calendar = userService.getCalendar(userCode);
        System.out.println("calendar321312 = " + calendar);

        return calendar;
    }

    @PostMapping("/user/submitReply")
    @ResponseBody
    public Map<String,Object> submitReply(@RequestBody Map<String,Object> info, Model model) {

        int userCode = ((UserDTO) model.getAttribute("userDTO")).getUserCode();
        int resReplyCode = (int) info.get("replyCode");
        info.put("userCode",userCode);
        int result = userService.submitReply(info);

        Map<String,Object> commentInfo = userService.getReplyComment(resReplyCode);

        System.out.println("commentInfo = " + commentInfo);

        return commentInfo;
    }


    @GetMapping("/user/contactList")
    public void contactList(Model model){

        List<ContactDTO> contactList = userService.contactList();

        model.addAttribute("contactList",contactList);

    }

    @GetMapping("/user/contactdetail")
    public String contactdetail(@RequestParam int contactCode ,Model model){
        String status = userService.findContactStatus(contactCode);
        if(status.equals("신규")) {
            userService.changeContact(contactCode);
        }
        ContactDTO contact = userService.selectContact(contactCode);
        model.addAttribute("contact",contact);

        return "user/contactdetail";
    }

    @PostMapping("/user/deletecontact")
    public String deleteContact(@RequestParam int contactCode,Model model){
       userService.deleteContact(contactCode);
       return "redirect:/user/contactList";
    }

    @PostMapping("/user/contactSearch")
    public String contactSearch(@RequestParam String contactCode ,@RequestParam String userName,Model model){

        if (contactCode == "" && userName == "") {
            List<ContactDTO> contactList = userService.contactList();
            model.addAttribute("contactList", contactList);
        } else if (userName == "") {
            List<ContactDTO> contactList = userService.selectContactByCode(Integer.parseInt(contactCode));
            model.addAttribute("contactList", contactList);
        } else {
            List<ContactDTO> contactList = userService.selectContactByName(userName);
            model.addAttribute("contactList", contactList);
        }
        return "user/contactList";
    }


    @GetMapping("/user/contact")
    public String contact(Model model) {
        String userName = ((UserDTO) model.getAttribute("userDTO")).getUserName();
        String email = ((UserDTO) model.getAttribute("userDTO")).getUserEmail();
        model.addAttribute("userName",userName);
        model.addAttribute("email",email);
        return "user/contact";
    }


    @PostMapping("/user/changeStatus")
    public String changeStatus(@RequestParam int contactCode){
        userService.changeStatus(contactCode);

        return "redirect:/user/contactdetail?contactCode=" + contactCode;
    }

    @PostMapping("/user/submitContact")
    @ResponseBody
    public int submitContact(@RequestBody Map<String,Object> info, Model model ){
        int userCode = ((UserDTO) model.getAttribute("userDTO")).getUserCode();
        info.put("userCode",userCode);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        info.put("nowTime",timestamp);
        int result = userService.submitContact(info);

        return result;
    }

    @PostMapping("/user/saveNote")
    @ResponseBody
    public void saveNote(@RequestBody Map<String,Object> info, Model model){
        int userCode = ((UserDTO) model.getAttribute("userDTO")).getUserCode();
        info.put("userCode",userCode);
        userService.saveNote(info);
    }

    @GetMapping("/user/getNote")
    @ResponseBody
    public Map<String,Object> getNote() {

        Map<String,Object> note = userService.getNote();

        return note;
    }
    @GetMapping(value = "/user/repairnoti", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public List<RepairDTO> repairnoti( Model model){
        int userCode = ((UserDTO) model.getAttribute("userDTO")).getUserCode();
        List<RepairDTO> repairList = userService.repairnoti(userCode);
        System.out.println("repairList = " + repairList);
        return repairList;
    }
    @GetMapping(value = "/user/partnoti",produces = "application/json; charset=UTF-8")
    @ResponseBody
    public List<PartDTO> partnoti( ){
        List<PartDTO> partList = userService.partnoti();
        return partList;
    }


}

