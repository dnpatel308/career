package com.brodos.devicecontext.service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brodos.devicecontext.DeviceContextApplication;
import com.brodos.devicecontext.model.dto.request.CreateOrUpdateDeviceConfigDTO;
import com.brodos.devicecontext.model.dto.response.DeviceConfigResponseDTO;
import com.brodos.devicecontext.model.dto.response.EmbeddedDeviceConfigsResponseDTO;
import com.brodos.devicecontext.service.assembler.ResponseAssembler;
import javax.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(DeviceContextApplication.CONTEXT_PATH + "/configurations")
public class DeviceConfigResource {

    @Autowired
    ResponseAssembler responseAssembler;

    @Autowired
    DeviceConfigService deviceConfigService;

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DeviceConfigResponseDTO createDeviceConfig(HttpServletRequest request,
        @RequestBody @Valid CreateOrUpdateDeviceConfigDTO createOrUpdateDeviceConfigDTO) {
        return responseAssembler.toDeviceConfigResponseDTO(request.getRequestURL().toString(),
            deviceConfigService.createDeviceConfig(createOrUpdateDeviceConfigDTO));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DeviceConfigResponseDTO getDeviceConfigById(HttpServletRequest request, @PathVariable("id") Long id) {
        return responseAssembler.toDeviceConfigResponseDTO(request.getRequestURL().toString(),
            deviceConfigService.getDeviceConfig(id));
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmbeddedDeviceConfigsResponseDTO getDeviceConfigByArticleNo(HttpServletRequest request, @NotBlank(
        message = "Article number is mandatory") @RequestParam("articlenumber") String articleNo) {
        DeviceConfigResponseDTO deviceConfigResponseDTO =
            responseAssembler.toDeviceConfigResponseDTO(request.getRequestURL().toString(),
                deviceConfigService.getByArticlenumber(articleNo));
        return responseAssembler.toEmbeddedDeviceConfigsResponseDTO(request, deviceConfigResponseDTO);
    }
}
