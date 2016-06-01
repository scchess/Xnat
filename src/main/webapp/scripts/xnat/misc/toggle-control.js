/* run this to populate the build info in the subjead of the Admin Site Info panel */
$.getJSON('/xapi/siteConfig/buildInfo').done(
  function(data) {
    for (key in data) {
      $('#xnat-build-info').append('<div class="panel-element panel-element-condensed"><label class="element-label">'+key+'</label> <span class="element-wrapper text-element-wrapper">'+data[key]+'</div></div>');
    }
  });


/* generic helper class for toggle links */
$('.toggle-control').on('click',function() {
  var target=$(this).data('toggle');
  $(target).toggle();
  $(this).find('.toggle-label').each(function() {
    var oldLabel = $(this).text();
    $(this).text($(this).data('alt'));
    $(this).data('alt',oldLabel)
  });
});

/* example construction:
  <div class="panel-heading">
    <h3 class="panel-title">
      Site Information
      <span class="pull-right">
        <a class="toggle-control" data-toggle="#xnat-build-info"><span class="toggle-label" data-alt="Hide">Show</span> Build Info</a>   // <-- toggle control
      </span>
    </h3>
  </div>

  <div class="panel-header-tray" id="xnat-build-info">
    <div class="panel-element panel-element-condensed">
      <label class="element-label">version</label>
      <span class="element-wrapper text-element-wrapper">1.7.0-SNAPSHOT</span>
    </div>
    <div class="panel-element panel-element-condensed">
      <label class="element-label">buildNumber</label>
      <span class="element-wrapper text-element-wrapper">Manual</span>
    </div>
    <div class="panel-element panel-element-condensed">
      <label class="element-label">Manifest-Version</label>
      <span class="element-wrapper text-element-wrapper">1.0</span>
    </div>
    <div class="panel-element panel-element-condensed">
      <label class="element-label">Application-Name</label>
      <span class="element-wrapper text-element-wrapper">XNAT</span>
    </div>
    <div class="panel-element panel-element-condensed">
      <label class="element-label">commit</label>
      <span class="element-wrapper text-element-wrapper">v245-g66534ec</span>
    </div>
    <div class="panel-element panel-element-condensed">
      <label class="element-label">buildDate</label>
      <span class="element-wrapper text-element-wrapper">Tue May 31 21:38:00 UTC 2016</span>
    </div>
  </div>
