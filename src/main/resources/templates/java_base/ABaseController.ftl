package ${package};

<#list imports as import>
import ${import};
</#list>

public class ABaseController {

    protected static final String STATUS_SUCCESS = "success";

    protected static final String STATUS_ERROR = "error";

    protected <T> ResponseVO success(T t) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUS_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    protected <T> ResponseVO error(BusinessException e, T t) {
        ResponseVO vo = new ResponseVO();
        vo.setStatus(STATUS_ERROR);
        if (e.getCode() == null) {
            vo.setCode(ResponseCodeEnum.CODE_600.getCode());
        } else {
            vo.setCode(e.getCode());
        }
        vo.setInfo(e.getMessage());
        vo.setData(t);
        return vo;
    }

    protected <T> ResponseVO error(T t) {
        ResponseVO vo = new ResponseVO();
        vo.setStatus(STATUS_ERROR);
        vo.setCode(ResponseCodeEnum.CODE_500.getCode());
        vo.setInfo(ResponseCodeEnum.CODE_500.getMsg());
        vo.setData(t);
        return vo;
    }
}