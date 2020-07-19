package com.example.sweater.controllers;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

//наследуется от Component, имеет дополнительные возможности
@Controller
public class MainController {

    @Autowired
    private MessageRepo messageRepo;

    //забирает из property путь
    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting(Model model){
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter, Model model){
        Iterable<Message> messages = null;

        if(filter != null && !filter.isEmpty()){
            messages = messageRepo.findByTag(filter);
        } else {
            messageRepo.findAll();
        }

        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);

        return "main";
    }

    @PostMapping ("/main")
    public String add(
            //user попадает из базы благодаря WebSecurityConfig
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag,
            @RequestParam("file") MultipartFile file, Model model) throws IOException {
        Message message = new Message(text, tag, user);

        if(file != null && !file.getOriginalFilename().isEmpty()){
            File uploadDir = new File(uploadPath);

            //если дирректории с файлами нет - она создается
            if(!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            //создание уникального имени файла
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            //загрузка файла
            file.transferTo(new File(uploadPath + "/" + resultFilename));

            message.setFilename(resultFilename);
        }

        messageRepo.save(message);

        Iterable<Message> messages = messageRepo.findAll();
        model.addAttribute("messages", messages);

        return "main";
    }

}
