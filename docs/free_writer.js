//COUNTDOWN OBJECTS
function CountDownTimer(duration, granularity) {
  this.duration = duration;
  this.granularity = granularity || 1000;
  this.tickFtns = [];
  this.running = false;
  this.pausing = 0; 
}

CountDownTimer.prototype.start = function() {
  if (this.running) {
    return;
  }
  this.running = true;
  var start = Date.now(),
      that = this,
      diff, obj;

  (function timer() {
    diff = that.duration - (((Date.now() - start) / 1000) | 0);

    if (diff > 0) {
      setTimeout(timer, that.granularity);
      that.pausing += 8; 
      if (that.pausing > 200) {
        that.pausing = 200; 
      }
    } else {
      diff = 0;
      that.running = false;
    }

    obj = CountDownTimer.parse(diff);
    that.tickFtns.forEach(function(ftn) {
      ftn.call(this, obj.minutes, obj.seconds);
    }, that);
  }());
};

CountDownTimer.prototype.onTick = function(ftn) {
  if (typeof ftn === 'function') {
    this.tickFtns.push(ftn);
  }
  return this;
};

CountDownTimer.prototype.expired = function() {
  return !this.running;
};

CountDownTimer.prototype.pauseAmount = function() {
    return this.pausing;
};

CountDownTimer.prototype.decreasePause = function() {
    this.pausing -= 2; 
    if (this.pausing < 0) {
        this.pausing = 0;
    }
}

CountDownTimer.parse = function(seconds) {
  return {
    'minutes': (seconds / 60) | 0,
    'seconds': (seconds % 60) | 0
  };
};

//MAIN
window.onload = function () {
    var writing_space = document.querySelector('#writing-space'),
        countdown = document.querySelector('#countdown'),
        motivationBar = document.querySelector("#progress"),
        timer = new CountDownTimer(300),
        timeObj = CountDownTimer.parse(300);

    format(timeObj.minutes, timeObj.seconds);
    
    timer.onTick(format);
    
    writing_space.onkeydown = function() {
        var key = event.keyCode || event.charCode; 
        if (key == 8 || key == 46) 
            return false; 
        if (key == 37 || key == 38 || key == 39 || key == 40)
            return false; 
    }

    writing_space.onclick = function() {
        var el = writing_space; 
        if (typeof el.selectionStart == "number") {
            el.selectionStart = el.selectionEnd = el.value.length; 
        } else if (typeof el.createTextRange != "undefined") {
            el.focus(); 
            var range = el.createTextRange(); 
            range.collapse(false); 
            range.select(); 
        }
    }

    writing_space.addEventListener('keyup', function () {
        timer.start();
        timer.decreasePause(); 
        motivationBar.style.width = timer.pauseAmount() + "%";
    });
    
    function format(minutes, seconds) {
        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;
        countdown.textContent = minutes + ':' + seconds;
        motivationBar.style.width = timer.pauseAmount() + "%";
    }
};