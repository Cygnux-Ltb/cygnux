package io.cygnuxltb.console.controller;

import io.cygnuxltb.console.controller.base.ServiceException;
import io.cygnuxltb.console.persistence.entity.ParamEntity;
import io.cygnuxltb.console.persistence.entity.StrategyEntity;
import io.cygnuxltb.console.service.ParamService;
import io.cygnuxltb.console.service.StrategyService;
import io.cygnuxltb.console.controller.util.RequestUtil;
import io.mercury.common.log.Log4j2LoggerFactory;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.mercury.common.http.MimeType.APPLICATION_JSON_UTF8;

@RestController
@RequestMapping(path = "/strategy")
public final class StrategyController {

    private final Logger log = Log4j2LoggerFactory.getLogger(getClass());

    @Resource
    private StrategyService strategyService;

    @Resource
    private ParamService paramService;

    /**
     * 返回全部Strategy
     *
     * @return ResponseEntity<List < StrategyEntity>>
     */
    @ExceptionHandler(ServiceException.class)
    @GetMapping
    public List<StrategyEntity> getAllStrategy() {
        return strategyService.getAllStrategy();
    }

    /**
     * 使用StrategyId作为get params访问Strategy
     *
     * @param strategyId int
     * @return ResponseEntity<StrategyEntity>
     */
    @ExceptionHandler(ServiceException.class)
    @GetMapping(path = "/{strategyId}")
    public StrategyEntity getStrategyById(@PathVariable("strategyId") int strategyId) {
        System.out.println("13131");
        return strategyService.getStrategy(strategyId);
    }

    /**
     * 使用StrategyId作为URI访问Param
     *
     * @param strategyId int
     * @return ResponseEntity<?>
     */
    @ExceptionHandler(ServiceException.class)
    @GetMapping("/{strategyId}/param")
    public List<ParamEntity> getParamsByStrategyId(@PathVariable("strategyId") int strategyId) {
        return paramService.getStrategyParams(strategyId);
    }

    /**
     * Put StrategyParam URI is StrategyId
     *
     * @param strategyId int
     * @return HttpServletRequest
     */
    @PutMapping(path = "/{strategyId}/param", consumes = APPLICATION_JSON_UTF8)
    public boolean putParamsByStrategyId(@PathVariable("strategyId") int strategyId,
                                         @RequestBody HttpServletRequest request) {
        var params = RequestUtil.bodyToObject(request, ParamEntity.class);
        log.info("putParamsByStrategyId recv : {}", params);
        return params != null && paramService.putStrategyParam(params);
    }

}