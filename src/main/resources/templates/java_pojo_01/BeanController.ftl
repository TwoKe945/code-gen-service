package ${package};

import lombok.RequiredArgsConstructor;

<#list imports as import>
import ${import};
</#list>

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

<#assign varBeanName=getBeanVarName(table.beanName), paramServiceName=getBeanVarName(table.beanName)+"Service">

/**
 * ${table.comment!} Controller
 */
@RestController("${varBeanName}Controller")
@RequiredArgsConstructor
@RequestMapping("/${varBeanName}")
public class ${table.beanName}Controller extends ABaseController {

   private final ${table.beanName}Service ${paramServiceName};


}