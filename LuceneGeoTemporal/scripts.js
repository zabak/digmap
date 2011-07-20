/**
 * Return an HTML element given ID
 *
 * @author Jorge Machado
 * @date April 2008
 *
 * params:
 * @objectId required object
 */
function getObjectById(objectId)
{
    // cross-browser function to get an object's style object given its id
    try
    {
        if(document.getElementById && document.getElementById(objectId))
        {
            // W3C DOM
            return document.getElementById(objectId);
        }
        else if (document.all(objectId))
        {
            // MSIE 4 DOM
            return document.all(objectId);
        }
        else if (document.layers && document.layers[objectId])
        {
            // NN 4 DOM.. note: this won't find nested layers
            return document.layers[objectId];
        }
        else
        {
            return false;
        }
    }
    catch(e)
    {
        return false;
    }
}

function hide(id,showId)
{
    hideOne(id);
    showOne(showId);
}
function show(id,hideId)
{
    hideOne(hideId);
    showOne(id);
}

function hideOne(id)
{
//    getObjectById(id).style.visibility='hidden';
//    getObjectById(id).style.position='absolute';
    getObjectById(id).style.display='none';
}

function showOne(id)
{
//    getObjectById(id).style.visibility='visible';
//    getObjectById(id).style.position='relative';
    getObjectById(id).style.display='';
}
function showOrHideOne(id)
{
    if(getObjectById(id).style.display == 'none')
        showOne(id);
    else
        hideOne(id);
}



function toggle(div_id) {
	var el = document.getElementById(div_id);
	if ( el.style.display == 'none' ) {	el.style.display = 'block';}
	else {el.style.display = 'none';}
}
function blanket_size(popUpDivVar) {
	if (typeof window.innerWidth != 'undefined') {
		viewportheight = window.innerHeight;
	} else {
		viewportheight = document.documentElement.clientHeight;
	}
	if ((viewportheight > document.body.parentNode.scrollHeight) && (viewportheight > document.body.parentNode.clientHeight)) {
		blanket_height = viewportheight;
	} else {
		if (document.body.parentNode.clientHeight > document.body.parentNode.scrollHeight) {
			blanket_height = document.body.parentNode.clientHeight;
		} else {
			blanket_height = document.body.parentNode.scrollHeight;
		}
	}
	var blanket = document.getElementById('blanket');
	blanket.style.height = blanket_height + 'px';
	var popUpDiv = document.getElementById(popUpDivVar);
	popUpDiv_height=blanket_height/2-150;//150 is half popup's height
    alert(popUpDivVar);
    popUpDiv.style.top = popUpDiv_height + 'px';
}
function window_pos(popUpDivVar) {
	if (typeof window.innerWidth != 'undefined') {
		viewportwidth = window.innerHeight;
	} else {
		viewportwidth = document.documentElement.clientHeight;
	}
	if ((viewportwidth > document.body.parentNode.scrollWidth) && (viewportwidth > document.body.parentNode.clientWidth)) {
		window_width = viewportwidth;
	} else {
		if (document.body.parentNode.clientWidth > document.body.parentNode.scrollWidth) {
			window_width = document.body.parentNode.clientWidth;
		} else {
			window_width = document.body.parentNode.scrollWidth;
		}
	}
	var popUpDiv = document.getElementById(popUpDivVar);
	window_width=window_width/2-150;//150 is half popup's width

    popUpDiv.style.left = window_width + 'px';
}
function popup(windowname) {
	blanket_size(windowname);
	window_pos(windowname);
	toggle('blanket');
	toggle(windowname);
}





/****************************
 *
 *
 *******************************/


    /**
     * Creates a new XmlHttpObject
     * @author Jorge Machado
     * @date April 2008
     *
     * params:
     * @handler target xmlHttpObject function
     */
    function GetXmlHttpObject(handler)
    {
        var objXmlHttp = null;
        if (navigator.userAgent.indexOf("Opera")>=0)
        {
            objXmlHttp=new XMLHttpRequest();
            objXmlHttp.onload=handler;
            objXmlHttp.onerror=handler;
            return objXmlHttp;
        }
        if (navigator.userAgent.indexOf("MSIE")>=0)
        {
            var strName="Msxml2.XMLHTTP";
            if (navigator.appVersion.indexOf("MSIE 5.5")>=0)
            {
                strName="Microsoft.XMLHTTP";
            }
            try
            {
                objXmlHttp=new ActiveXObject(strName);
                objXmlHttp.onreadystatechange=handler ;
                return objXmlHttp;
            }
            catch(e)
            {
                alert("Error. Scripting for ActiveX might be disabled") ;
                return objXmlHttp;
            }
        }
        if (navigator.userAgent.indexOf("Mozilla")>=0)
        {
            objXmlHttp=new XMLHttpRequest();
            objXmlHttp.onload=handler;
            objXmlHttp.onerror=handler;
            return objXmlHttp;
        }
    }

    /**
     * Starts XMLHTTP Ajax request
     *
     * @author Jorge Machado
     * @date April 2008
     *
     * params:
     * @params HTTP GET Parameters for query string
     * @xmlHttpRequest request
     * @id target element
     * @stateChanged target function
     * @innerPhrase to put while waiting for response
     * @navjsp requested jsp
     */
    function startRequest(xmlHttpRequest,params,id,stateChanged,innerPhrase,navjsp)
    {
        var contentType = "application/x-www-form-urlencoded; charset=UTF-8";
        if(innerPhrase != '')
            getObjectById(id).innerHTML = innerPhrase;
        if (xmlHttpRequest==null)
        {
            alert ("Browser does not support HTTP Request");
            return;
        }
        var finalParams = "";
        var url = location.href.substring(0,location.href.indexOf('/',location.href.indexOf("://")+3)) + navjsp;
        if(params != null && params.length > 0)
        {
            //url=url+"?"+ params;
            //url=url+"&sid="+Math.random();
            url=url+"?sid="+Math.random();
            var paramsArray = params.split("&");
            var i;
            var union = '';
            for(i = 0; i < paramsArray.length; i++)
            {
                var parameter = paramsArray[i].split("=");
                if(parameter.length == 2)
                {
                    finalParams += union + parameter[0] + '=' + encodeURIComponent(parameter[1]);
                    union = '&';
                }
            }
        }
        else
        {
            url=url+"?sid="+Math.random();
        }

        xmlHttpRequest.onreadystatechange=stateChanged;
        xmlHttpRequest.open("POST",url,true);
        xmlHttpRequest.setRequestHeader("Content-Type", contentType);
        xmlHttpRequest.send(finalParams);
    }
    var xmlHttpRelevance;

    var xmlHttpTopFlashNews;
    var getFlashNewsJsp;
    var semaphoreFlashNews = 0;
    

    function getFlashNews(jsp,params)
    {
//        alert(jsp);
        getFlashNewsJsp = jsp;
        semaphoreFlashNews = 1;
        getFlashNewsTimeoutCall(params);
    }
    function getFlashNewsTimeoutCall(params)
    {
        xmlHttpTopFlashNews=GetXmlHttpObject(stateChangedGetFlashNews);
        semaphoreFlashNews = 1;
        startRequest(xmlHttpTopFlashNews,params,"flashTopNews",stateChangedGetFlashNews,"",getFlashNewsJsp)
    }
    function stateChangedGetFlashNews()
    {
        if (semaphoreFlashNews == 1 && (xmlHttpTopFlashNews.readyState==4 || xmlHttpTopFlashNews.readyState=="complete"))
        {
            if(xmlHttpTopFlashNews.responseText.indexOf("FAIL")>=0)
            {
                   alertAjax();
                   getObjectById("flashTopNews").innerHTML=xmlHttpTopFlashNews.responseText;
            }
            else
            {
                var index = xmlHttpTopFlashNews.responseText.indexOf("OK");
                var response = xmlHttpTopFlashNews.responseText.substring(index);
                var fields = response.split(";");
                if(fields.length > 1)
                {

                    var doc = fields[1];
//                    alert(xmlHttpTopFlashNews.responseText);
                    getObjectById("feedback"+doc).innerHTML="<label style=\"background-color:#90ee90;padding:5px\">SAVED</label>";
                }


            }
//            getObjectById("flashTopNews").innerHTML=xmlHttpTopFlashNews.responseText;
            semaphoreFlashNews = 0;
        }
    }
    var cantUse = "";
    function publishJudgement(form,doc,relevance)
    {
//        alert(form.id_topic.value);
        var topic = form.id_topic[1].value;

        if(!topic || topic == "" || topic == null || topic == "undefined")
        {
            if(form.id_topic.selectedIndex) topic = form.id_topic.selectedIndex.value;
            if(!topic || topic == "" || topic == null || topic == "undefined")
            {
                if(form.id_topic.selectedIndex && form.id_topic.options)
                    topic = form.id_topic.options[form.id_topic.selectedIndex].value;
            }
        }
         if(!topic || topic == "" || topic == null || topic == "undefined")
        {
           alertAjax();
        }
        getObjectById("feedback"+doc).innerHTML="<img src=\"wait.gif\"/>";
        getFlashNews("/lgte/assessmentsNtcirService.jsp?","op=addJudgments&id_topic="+topic+"&"+doc+"="+relevance+"&obs"+doc+"="+form['obs'+doc].value);
    }

    function alertAjax()
    {
        if(cantUse == "")
            alert("Sorry LGTE is returning an error when is calling AJAX Services in order to record your judgement, please use the yellow button to save your work from times to times (sessions &lt; 30 minutos), or when you change your topic docs list");
        cantUse = "true";
    }