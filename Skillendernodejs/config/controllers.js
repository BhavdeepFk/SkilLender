var loginPage=function(request,reply){
    console.log("At Homepage");
	var context={
		hideNav:true
    }
    reply.view('login.html',context);
};

var homePage=function(request,reply){
    reply.view('home.html');
};

var searchPage=function(request,reply){
    var context={
        searchKey:request.params.searchKey
    }
    reply.view('search.html',context);
}

var userhome=function(request,reply){
    reply.view('userhome.html');
};


module.exports=[
    {
        method:'GET',
        path:'/',
        handler:loginPage
    },
    {
        method:'GET',
        path:'/home',
        handler:homePage
    },
	{
        method:'GET',
        path:'/userhome',
        handler:userhome
    },
    {
        method:'GET',
        path:'/search/{searchKey}',
        handler:searchPage
    },
	
    {
        method:'GET',
        path:'/static/{path*}',
        handler:{
            directory:{
                path:'./static',
                listing:false,
                index:false
            }
        }
    }
	
]