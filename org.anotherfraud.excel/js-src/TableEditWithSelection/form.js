(table_Edit_With_Selection = function() {

    let _representation;
    let _value;

    let view = {};
	

    view.init = function(representation, value) {
        _representation = representation;
        _value = value;

        createUI();
    };

    view.getComponentValue = () => {
        _value.firstName = document.getElementById("firstName").value;
        _value.lastName = document.getElementById("lastName").value;
		
        return _value;
    };






function createUI() {
    let body = document.getElementsByTagName("body")[0];
    body.innerHTML = `<div class="container-fluid">
      <form class="form-horizontal">
        <div class="form-group">
          <label class="col-sm-2 control-label">First name</label>
          <div class="col-sm-10">
            <input type="text" class="form-control" id="firstName">
          </div>
        </div>
        <div class="form-group">
          <label class="col-sm-2 control-label">Last name</label>
          <div class="col-sm-10">
            <input type="text" class="form-control" id="lastName">
          </div>
        </div>
      </form>
    </div>
	<div id="example-table"></div>
	`;
	
	var table = new Tabulator("#example-table", {
		height:"311px",
		columns:[
		{title:"Name", field:"name"},
		{title:"Progress", field:"progress", sorter:"number"},
		{title:"Gender", field:"gender"},
		{title:"Rating", field:"rating"},
		{title:"Favourite Color", field:"col"},
		{title:"Date Of Birth", field:"dob", hozAlign:"center"},
		],
	});
	
}

    return view;
}());