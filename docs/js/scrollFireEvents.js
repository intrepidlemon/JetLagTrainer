fireShowLeft = function(selector) {
    $(selector).velocity(
        { translateX: "-100px"},
        { duration: 0 });

    $(selector).velocity(
        { opacity: "1", translateX: "0"},
        { duration: 800, easing: [60, 10] });
  };

fireShowRight = function(selector) {
    $(selector).velocity(
        { translateX: "100px"},
        { duration: 0 });

    $(selector).velocity(
        { opacity: "1", translateX: "0"},
        { duration: 800, easing: [60, 10] });
  };
 $(document).ready(function() {
    var options = [
    {selector: '#home-screen', offset: 400, callback: 'fireShowLeft("#home-screen")' },
    {selector: '#introduction', offset: 500, callback: 'fireShowRight("#introduction")' }
  ];

   Materialize.scrollFire(options);
 });


