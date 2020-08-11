package com.example.sweater.controllers;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
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
            messages = messageRepo.findAll();
        }

        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);

        return "main";
    }

    @PostMapping ("/main")
    public String add(
            //user попадает из базы благодаря WebSecurityConfig
            @AuthenticationPrincipal User user,
            //Valid запускает валидацию для message
            @Valid @ModelAttribute("newMessage") Message message,
            //получение аргументов и списка сообщений об ошибке для валидации. Дложен всегда идти перед Model
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file, Model model) throws IOException {
        message.setAuthor(user);

        if(bindingResult.hasErrors()){
            //преобразовывает List ошибок в Map с ключом поля ошибки и значением в виде самой ошибки
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("newMessage", message);
        } else {
            if (file != null && !file.getOriginalFilename().isEmpty()) {
                File uploadDir = new File(uploadPath);

                //если дирректории с файлами нет - она создается
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                //создание уникального имени файла
                String uuidFile = UUID.randomUUID().toString();
                String resultFilename = uuidFile + "." + file.getOriginalFilename();

                //загрузка файла
                file.transferTo(new File(uploadPath + "/" + resultFilename));

                message.setFilename(resultFilename);
            }
            model.addAttribute("newMessage", null);

            messageRepo.save(message);
        }

        Iterable<Message> messages = messageRepo.findAll();
        model.addAttribute("messages", messages);

        return "main";
    }

}
