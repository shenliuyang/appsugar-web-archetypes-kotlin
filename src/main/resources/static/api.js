export  function get( params){
    params.method = "get";
    request(params);
}
export  function post(params){
    params.method = "post";
    if(params.headers==null)params.headers = {};
    params.headers['Content-Type'] = "application/x-www-form-urlencoded"
    request(params);
}
export function postJson(params){
    params.method = "post";
    if(params.headers==null)params.headers = {};
    params.headers['Content-Type'] = "application/json;charset=UTF-8"
    request(params);
}

async function  request(params){
    //尝试带上授权参数
    var auth = localStorage.authorization
    if(auth){
        if(params.headers==null)params.headers= {};
        if(params.headers.authorization==null)params.headers.authorization = auth;
    }
    try{
        var response = await axios(params);
        if(response.headers.authorization != null){
            //保存授权信息
            localStorage.authorization = response.headers.authorization
        }
        if(params.successResponse == null && handleException(response,params.vm)){
            return;
        }
        if(params.success!=null){
            params.success(response.data);
        }
        if(params.successResponse!=null){
            params.successResponse(response);
        }
    }catch(ex){
        if(params.error != null){
            params.error(ex);
        }else{
            console.log(ex)
            //TODO 通用异常处理
        }
    }
}

function handleException(res,vm){
    if(res.headers.error == "1"){
        var data = res.data;
        var code = data.code
        console.log(res.config.url+" : "+data.msg)
        INSTANCE.vm.config.globalProperties.$notify.error({title: '错误',message: data.msg });
        if(code == -200 || code == -201){
            //需要登录,请先登录
            INSTANCE.router.push('/login');
        }else if (code == -202){
            INSTANCE.router.push('/login');
        }else if (code == -203){
            //权限不足
        }
        return true;
    }
    return false;
}
const INSTANCE = {
    name:"Api"
}
export default INSTANCE