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