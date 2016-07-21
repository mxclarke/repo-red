
(function( mxcfoo, $, undefined ) {
    //Private Property
//    var isHot = true;

    //Public Property
 //   mxcfoo.ingredient = "Bacon Strips";

    //Public Method
	// The ID column is hidden
    mxcfoo.setUpTable = function(id, dataList) {
    	  	  	
		$(id).footable({
			"toggleColumn": "last",
			"expandFirst": true,
			"columns": [
				{ "name": "id", "title": "ID", "visible": false, "breakpoints": "xs" },
				{ "name": "firstName", "title": "First Name" },
				{ "name": "lastName", "title": "Last Name" },
				{ "name": "title", "title": "Title" },
				
				{ "name": "emailAddress", "title": "Email", "breakpoints": "xs" },
				{ "name": "salary", "title": "Salary", "breakpoints": "xs sm" },
				//{ "name": "dob", "title": "DOB", "breakpoints": "xs sm md" },
				{ "name": "editBn", "title": "Edit"}
			],
			"rows": dataList
			// the following, an attempt for serverside paging, doesn't work in Foo because
			// although it is calling the controller method, it isn't passing through
			// startIdx, length, filtering and sorting information, or anything else, you
			// would have to somehow construct this and post it through
			//"rows" : $.get("/lecturers/getPagedLecturers")
		});	
		
    };

    //Private Method
//    function addItem( item ) {
//        if ( item !== undefined ) {
//            console.log( "Adding " + $.trim(item) );
//        }
//    }
}( window.mxcfoo = window.mxcfoo || {}, jQuery ));

