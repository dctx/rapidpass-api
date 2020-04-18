/*
 * Copyright (c) 2020.  DevConnect Philippines, Inc.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed 
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 * See the License for the specific language governing permissions and limitations under the License.
 */

package ph.devcon.rapidpass.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ph.devcon.rapidpass.RapidpassApplication;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@WebAppConfiguration
@SpringBootTest(classes = RapidpassApplication.class)
public abstract class BaseApiTest
{
    protected MockMvc mvc;
    @Autowired
    WebApplicationContext webApplicationContext;
    
    @BeforeEach
    public void setup()
    {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    protected String mapToJson(Object obj) throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        objectMapper.setDateFormat(dateFormat);
        return objectMapper.writeValueAsString(obj);
    }
    
    protected <T> T mapFromJson(String json, Class<T> clazz)
        throws JsonParseException, JsonMappingException, IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        // Now you should use JavaTimeModule instead
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper.readValue(json, clazz);
    }
    
    protected MvcResult postData(String postJson, String urlTemplate, Object... parameters) throws Exception
    {
        return mvc.perform(MockMvcRequestBuilders.post(urlTemplate,parameters)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .content(postJson)).andReturn();
    }
    
    protected MvcResult getData(String urlTemplate, Object... parameters) throws Exception
    {
        return mvc.perform(MockMvcRequestBuilders.get(urlTemplate, parameters)
            .accept(MediaType.APPLICATION_JSON)).andReturn();
    }
    
    protected MvcResult putData(String putData, String urlTemplate) throws Exception
    {
        return mvc.perform(MockMvcRequestBuilders.put(urlTemplate)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(putData)
        ).andReturn();
    }
    
    protected MvcResult patchData(String patchJson, String urlTemplate) throws Exception
    {
        return mvc.perform(MockMvcRequestBuilders.patch(urlTemplate)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(patchJson)
        ).andReturn();
    }
    
    protected MvcResult deleteData(String urlTemplate) throws Exception
    {
        return mvc.perform(MockMvcRequestBuilders.delete(urlTemplate)
            .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
    }
}
