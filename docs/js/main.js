var draw = function (path, length, start, stop) {
    var current_frame = 0; 
    var handle = 0;

    var drawing = function () {
        var progress = current_frame/stop;
        var percent_length = (current_frame-start)/(stop-start);
      
        if (progress > 1) {
            window.cancelAnimationFrame(handle);
        } else if(current_frame < start){
            current_frame++;
            handle = window.requestAnimationFrame(drawing);
        }   else {
            current_frame++;
            path.style.strokeDashoffset = Math.floor(length * (1 - percent_length));
            handle = window.requestAnimationFrame(drawing);
        }
    };
    drawing();
}

var erase = function(path, length) {
    path.style.strokeDasharray = length + ' ' + length; 
    path.style.strokeDashoffset = length;
}


$(document).ready(function(){
	var hexagon = document.getElementById("logo_border"); 
	var lines = hexagon.getElementsByClassName("line");  

	frames = 80; 
	length = 240; 

	for(i = 0; i<6; i++){
		erase(lines[i], length);
	}
	for(i = 0; i < 6; i++){
		draw(lines[i], length, 0, frames);
	}
	$('.modal-trigger').leanModal();
});