package com.example.demo.web;

import com.example.demo.entities.Patient;
import com.example.demo.repositories.PatientRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@AllArgsConstructor
public class PatientController {
    PatientRepository patientRepository;

    @GetMapping( "/user/index")
    public String patients(Model model,
                           @RequestParam(name = "page",defaultValue = "0")int page,
                           @RequestParam(name = "size",defaultValue = "7")int size,
                            @RequestParam(name = "keyword",defaultValue = "")String keyword){
        Page<Patient> patientpage=patientRepository.findByNomContains(keyword,PageRequest.of(page, size));
        model.addAttribute("listepatient",patientpage.getContent());
        model.addAttribute("pages",new int[patientpage.getTotalPages()]);
        model.addAttribute("currentpage",page);
          return "patient";
    }
@GetMapping("/admin/delete")
   @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String delete(long id,String keyword,Long page){
        patientRepository.deleteById(id);
        return "redirect:/user/index?page="+page+"&keyword"+keyword;



    }



    @GetMapping("/")
    public String home(){
        return "redirect:/user/index";

    }

@GetMapping("/patients")
@ResponseBody
    public List<Patient> patientList(){
        return patientRepository.findAll();

    }


    @GetMapping("/admin/formPatient")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String formPatient(Model model){
        model.addAttribute("patient",new Patient());
        return "formPatient";
    }

    @PostMapping("/admin/save")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String save(@Valid Patient patient , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "formPatient";
        patientRepository.save(patient);
        return "redirect:/user/index";
    }
    @GetMapping(path = "/admin/editPatient")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editPatient(Model model, Long id){
        Patient patient=patientRepository.findById(id).orElse(null);
        if (patient==null) throw new RuntimeException("Patient introuvable");
        model.addAttribute("patient",patient);
        return "editPatient";
    }

}
