package com.nnk.springboot.controllerTests;

import com.nnk.springboot.controllers.CurveController;
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurveController.class)
public class CurveControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CurvePointRepository  curvePointRepository;

    @Test
    @WithMockUser(roles = "USER")
    public void homeTest() throws Exception {

        when(curvePointRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/curvePoint/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/list"))
                .andExpect(content().string(containsString("Curve Point List")));

        verify(curvePointRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void addCurveFormTest() throws Exception {

        this.mockMvc.perform(get("/curvePoint/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(content().string(containsString("Add New Curve Point")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void validateTest() throws Exception {

        CurvePoint validCurvePoint = new CurvePoint(10d, 10d);
        when(curvePointRepository.save(any())).thenReturn(validCurvePoint);
        when(curvePointRepository.findAll()).thenReturn(new ArrayList<>());


        this.mockMvc.perform(post("/curvePoint/validate")
                        .flashAttr("curvePoint", validCurvePoint)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"));

        verify(curvePointRepository).save(any());
        verify(curvePointRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void validateTest_withErrors() throws Exception {

        CurvePoint invalidCurvePointTerm = new CurvePoint(0, 10d);
        CurvePoint invalidCurvePointValue = new CurvePoint(10d, 0);

        this.mockMvc.perform(post("/curvePoint/validate")
                        .flashAttr("curvePoint", invalidCurvePointTerm)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(content().string(containsString("Add New Curve Point")))
                .andExpect(model().attributeHasFieldErrors("curvePoint", "term"));

        this.mockMvc.perform(post("/curvePoint/validate")
                        .flashAttr("curvePoint", invalidCurvePointValue)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(content().string(containsString("Add New Curve Point")))
                .andExpect(model().attributeHasFieldErrors("curvePoint", "value"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void showUpdateFormTest() throws Exception {

        CurvePoint validCurvePoint = new CurvePoint(10d, 10d);
        when(curvePointRepository.findById(anyInt())).thenReturn(Optional.of(validCurvePoint));

        this.mockMvc.perform(get("/curvePoint/update/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(content().string(containsString("Update CurvePoint")));

        verify(curvePointRepository).findById(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void updateCurveTest() throws Exception {

        CurvePoint validCurvePoint = new CurvePoint(10d, 10d);
        when(curvePointRepository.save(any())).thenReturn(validCurvePoint);
        when(curvePointRepository.findAll()).thenReturn(new ArrayList<>());


        this.mockMvc.perform(post("/curvePoint/update/{id}", 1)
                        .flashAttr("curvePoint", validCurvePoint)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"));

        verify(curvePointRepository).save(any());
        verify(curvePointRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void updateCurveTest_withErrors() throws Exception {

        CurvePoint invalidCurvePointTerm = new CurvePoint(0, 10d);
        CurvePoint invalidCurvePointValue = new CurvePoint(10d, 0);

        this.mockMvc.perform(post("/curvePoint/update/{id}", 1)
                        .flashAttr("curvePoint", invalidCurvePointTerm)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(content().string(containsString("Update CurvePoint")))
                .andExpect(model().attributeHasFieldErrors("curvePoint", "term"));

        this.mockMvc.perform(post("/curvePoint/update/{id}", 1)
                        .flashAttr("curvePoint", invalidCurvePointValue)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(content().string(containsString("Update CurvePoint")))
                .andExpect(model().attributeHasFieldErrors("curvePoint", "value"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteCurveTest() throws Exception {

        CurvePoint validCurvePoint = new CurvePoint(10d, 10d);
        when(curvePointRepository.findById(anyInt())).thenReturn(Optional.of(validCurvePoint));
        doNothing().when(curvePointRepository).delete(any());
        when(curvePointRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/curvePoint/delete/{id}", 1))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"));

        verify(curvePointRepository).findById(1);
        verify(curvePointRepository).delete(any());
        verify(curvePointRepository).findAll();
    }
    
    
    
    
    
}
