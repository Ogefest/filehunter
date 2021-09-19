(this["webpackJsonpfilehunter-front"]=this["webpackJsonpfilehunter-front"]||[]).push([[0],{24:function(e,t,n){},25:function(e,t,n){},38:function(e,t,n){"use strict";n.r(t);var s=n(1),a=n.n(s),c=n(16),i=n.n(c),r=(n(24),n(25),n(26),n(19)),l=n(6),o=n(2),d=n(3),h=n(5),j=n(4),b=n(7),u=n(0),x=function(e){Object(h.a)(n,e);var t=Object(j.a)(n);function n(e){var s;return Object(o.a)(this,n),(s=t.call(this,e)).refreshInterval=1e4,s.refreshStatus=s.refreshStatus.bind(Object(b.a)(s)),s}return Object(d.a)(n,[{key:"componentDidMount",value:function(){this.refreshStatus()}},{key:"refreshStatus",value:function(){var e=this;fetch("".concat("","/system/status")).then((function(e){return e.json()})).then((function(t){e.result=t,setTimeout(e.refreshStatus,e.refreshInterval),e.setState({})}))}},{key:"render",value:function(){return void 0===this.result?"":""===this.result.currentTask?(this.refreshInterval=1e4,""):(this.refreshInterval=1e3,Object(u.jsxs)("span",{class:"badge bg-success",children:["Task ",this.result.currentTask]}))}}]),n}(a.a.Component),m=function(e){Object(h.a)(n,e);var t=Object(j.a)(n);function n(){return Object(o.a)(this,n),t.apply(this,arguments)}return Object(d.a)(n,[{key:"render",value:function(){return Object(u.jsx)("nav",{class:"navbar navbar-expand-lg navbar-light",children:Object(u.jsxs)("div",{class:"container-fluid",children:[Object(u.jsx)("span",{class:"badge bg-primary",children:"FH"})," ",Object(u.jsx)("a",{class:"navbar-brand",href:"/",children:"Filehunter"}),Object(u.jsx)("button",{class:"navbar-toggler",type:"button","data-bs-toggle":"collapse","data-bs-target":"#navbarSupportedContent","aria-controls":"navbarSupportedContent","aria-expanded":"false","aria-label":"Toggle navigation",children:Object(u.jsx)("span",{class:"navbar-toggler-icon"})}),Object(u.jsxs)("div",{class:"collapse navbar-collapse",id:"navbarSupportedContent",children:[Object(u.jsxs)("ul",{class:"navbar-nav ms-auto mb-1 mb-lg-0",children:[Object(u.jsx)("li",{class:"nav-item",children:Object(u.jsxs)("a",{class:"nav-link","aria-current":"page",href:"/",children:[Object(u.jsx)("i",{class:"bi bi-search"})," Search"]})}),Object(u.jsx)("li",{class:"nav-item",children:Object(u.jsxs)("a",{href:"/gui/index",class:"nav-link menu-item nav-active",children:[Object(u.jsx)("i",{class:"bi bi-folder2-open"})," Indexes"]})}),Object(u.jsx)("li",{class:"nav-item",children:Object(u.jsxs)("a",{rel:"noreferrer",href:"".concat("","/api-doc"),target:"_blank",class:"nav-link menu-item",children:[Object(u.jsx)("i",{class:"bi bi-code-slash"})," API Doc"]})}),Object(u.jsx)("li",{class:"nav-item",children:Object(u.jsxs)("a",{rel:"noreferrer",href:"https://github.com/Ogefest/filehunter",target:"_blank",class:"nav-link  menu-item",children:[Object(u.jsx)("i",{class:"bi bi-github"})," Github"]})})]}),Object(u.jsx)(x,{})]})]})})}}]),n}(a.a.Component),p=function(e){Object(h.a)(n,e);var t=Object(j.a)(n);function n(e){var s;return Object(o.a)(this,n),(s=t.call(this,e)).getColorByName=s.getColorByName.bind(Object(b.a)(s)),s}return Object(d.a)(n,[{key:"getColorByName",value:function(e){for(var t=0,n=0;n<e.length;n++)t=e.charCodeAt(n)+((t<<5)-t);var s="#";for(n=0;n<3;n++){s+=("00"+(t>>8*n&255).toString(16)).substr(-2)}return s}},{key:"render",value:function(){var e="bi-file-earmark";"d"===this.props.params.type&&(e="bi-folder2");this.getColorByName(this.props.params.indexname);return Object(u.jsxs)("div",{class:"row mb-3",children:[Object(u.jsxs)("h5",{children:[Object(u.jsx)("i",{class:"bi ".concat(e)})," ",Object(u.jsx)("a",{rel:"noreferrer",target:"_blank",href:"".concat("","/download/").concat(this.props.params.uuid),children:this.props.params.name})]}),Object(u.jsx)("small",{class:"text-success",children:this.props.params.path})]},this.props.params.uuid)}}]),n}(a.a.Component),O=function(e){Object(h.a)(n,e);var t=Object(j.a)(n);function n(){return Object(o.a)(this,n),t.apply(this,arguments)}return Object(d.a)(n,[{key:"render",value:function(){return Object(u.jsx)("div",{class:"container",children:Object(u.jsx)("div",{class:"row",children:Object(u.jsx)("div",{class:"col-8 offset-4",children:Object(u.jsx)("div",{class:"alert alert-info col-md-12 col-md-offset-12",align:"center",children:"Empty search results"})})},"empty-results")})}}]),n}(a.a.Component),f=function(e){Object(h.a)(n,e);var t=Object(j.a)(n);function n(e){var s;return Object(o.a)(this,n),(s=t.call(this,e)).indexList=[],s.indexName="",s.extension="",s}return Object(d.a)(n,[{key:"componentDidMount",value:function(){var e=this;fetch("".concat("","/index/list")).then((function(e){return e.json()})).then((function(t){e.indexList=t,e.requestFinished=!0,e.setState({})}))}},{key:"handleIndexNameChange",value:function(e){this.indexName=e.target.value,this.handleAddFilters()}},{key:"handleExtensionChange",value:function(e){this.extension=e.target.value,this.handleAddFilters()}},{key:"handleAddFilters",value:function(){var e="";""!==this.indexName&&(e+=" AND indexname:"+this.indexName+" "),""!==this.extension&&(e+=" AND ext:"+this.extension+" "),this.props.update(e)}},{key:"render",value:function(){return Object(u.jsx)("div",{children:Object(u.jsxs)("div",{class:"row",id:"",children:[Object(u.jsx)("div",{class:"col-3",children:Object(u.jsxs)("div",{class:"input-group input-group-sm mb-3",children:[Object(u.jsx)("label",{class:"input-group-text",for:"selectIndexname",children:"Index"}),Object(u.jsxs)("select",{onChange:this.handleIndexNameChange.bind(this),id:"selectIndexname",class:"form-select form-select-sm","aria-label":"Index name",children:[Object(u.jsx)("option",{}),this.indexList.map((function(e){return Object(u.jsx)("option",{value:e.name,children:e.name})}))]})]})}),Object(u.jsx)("div",{class:"col-3",children:Object(u.jsxs)("div",{class:"input-group input-group-sm mb-3",children:[Object(u.jsx)("label",{class:"input-group-text",for:"inputExtension",children:"Extension"}),Object(u.jsx)("input",{onChange:this.handleExtensionChange.bind(this),type:"text",class:"form-control","aria-label":"extension","aria-describedby":"extension"})]})})]})})}}]),n}(a.a.Component),v=function(e){Object(h.a)(n,e);var t=Object(j.a)(n);function n(e){var s;return Object(o.a)(this,n),(s=t.call(this,e)).state={value:null,searchResultRows:[]},s.sr=[],s.lastQuery="",s.currentQuery="",s.advancedFilterQuery="",s.activeQuery=!1,s.searchTimeout=null,s.updateAdvancedFilterQuery=s.updateAdvancedFilterQuery.bind(Object(b.a)(s)),s}return Object(d.a)(n,[{key:"componentDidMount",value:function(){}},{key:"componentWillUnmount",value:function(){clearInterval(this.intervalId)}},{key:"updateAdvancedFilterQuery",value:function(e){this.advancedFilterQuery=e,""==this.currentQuery&&(this.advancedFilterQuery=this.advancedFilterQuery.trim().substring(3)),this.handleSearch()}},{key:"handleSearch",value:function(){var e=this,t=this.currentQuery+" "+this.advancedFilterQuery;""!==t&&t!==this.lastQuery&&!0!==this.activeQuery&&(this.activeQuery=!0,fetch("".concat("","/search?q=").concat(encodeURIComponent(t))).then((function(e){return e.json()})).then((function(n){e.lastQuery=t,e.activeQuery=!1,e.sr=[],n.map((function(t){return e.sr.push(Object(u.jsx)(p,{params:t}))})),0===e.sr.length&&e.sr.push(Object(u.jsx)(O,{})),e.setState({searchResultRows:e.sr})})))}},{key:"handleInputChange",value:function(e){this.currentQuery=e.target.value,null!=this.searchTimeout&&clearTimeout(this.searchTimeout),this.searchTimeout=setTimeout(this.handleSearch.bind(this),300)}},{key:"render",value:function(){return Object(u.jsxs)("div",{class:"container mt-5",children:[Object(u.jsx)("div",{class:"row",children:Object(u.jsx)("div",{class:"col-8 offset-2",children:Object(u.jsxs)("div",{class:"input-group mb-3",children:[Object(u.jsx)("input",{autoFocus:!0,type:"text",class:"form-control",placeholder:"Search for files","aria-label":"Search for files","aria-describedby":"button-addon2",onChange:this.handleInputChange.bind(this)}),Object(u.jsx)("button",{class:"btn btn-primary",type:"button",onClick:this.handleSearch.bind(this),children:Object(u.jsx)("i",{class:"bi bi-search"})}),Object(u.jsx)("button",{class:"btn btn-outline-secondary",type:"button","data-bs-toggle":"collapse","data-bs-target":"#filtersForm",children:Object(u.jsx)("i",{class:"bi bi-filter"})})]})})}),Object(u.jsx)("div",{class:"row collapse mb-3",id:"filtersForm",children:Object(u.jsx)("div",{class:"col-8 offset-2",children:Object(u.jsx)(f,{update:this.updateAdvancedFilterQuery})})}),Object(u.jsx)("div",{class:"row",children:Object(u.jsx)("div",{class:"col-6 offset-2",children:this.state.searchResultRows})})]})}}]),n}(a.a.Component),g=function(e){Object(h.a)(n,e);var t=Object(j.a)(n);function n(e){var s;return Object(o.a)(this,n),s=t.call(this,e),console.log(e),s.state={clicked:!1},s.handleClick=s.handleClick.bind(Object(b.a)(s)),s}return Object(d.a)(n,[{key:"handleClick",value:function(){var e=this;if(!0!==this.state.clicked)return fetch("".concat("","/index/reindex/").concat(this.props.indextype),{method:"POST",headers:{"Content-Type":"application/json"}}).then((function(t){e.setState({clicked:!0})})),!1}},{key:"render",value:function(){return Object(u.jsx)("button",{"data-placement":"top",title:"Reindex",class:"btn btn-sm btn-success",href:"#",onClick:this.handleClick,children:Object(u.jsx)("i",{class:"bi bi-arrow-repeat"})})}}]),n}(a.a.Component),y=function(e){Object(h.a)(n,e);var t=Object(j.a)(n);function n(){return Object(o.a)(this,n),t.apply(this,arguments)}return Object(d.a)(n,[{key:"render",value:function(){return Object(u.jsx)("div",{class:"container mt-5",children:Object(u.jsx)("div",{class:"row",children:Object(u.jsxs)("div",{class:"col-6 offset-3",children:[Object(u.jsx)("div",{class:"alert alert-info col-md-12 col-md-offset-12",align:"center",children:"You have no indexed directories. Click button below to add directory."}),Object(u.jsx)("div",{align:"center",children:Object(u.jsxs)("a",{href:"/gui/index/create",class:"btn btn-lg btn-success",children:[Object(u.jsx)("i",{class:"bi bi-plus"})," Create new index"]})})]})},"empty-results")})}}]),n}(a.a.Component),k=function(e){Object(h.a)(n,e);var t=Object(j.a)(n);function n(e){var s;return Object(o.a)(this,n),(s=t.call(this,e)).result=[],s.requestFinished=!1,s.handleRemove=s.handleRemove.bind(Object(b.a)(s)),s}return Object(d.a)(n,[{key:"componentDidMount",value:function(){this.refreshList()}},{key:"refreshList",value:function(){var e=this;fetch("".concat("","/index/list")).then((function(e){return e.json()})).then((function(t){e.result=t,e.requestFinished=!0,e.setState({})}))}},{key:"handleRemove",value:function(e){var t=this;return window.confirm("Are you sure?")&&fetch("".concat("","/index/remove/").concat(e.name),{method:"DELETE",headers:{"Content-Type":"application/json"}}).then((function(e){return e.json()})).then((function(e){t.result=e,t.setState({})})),!1}},{key:"render",value:function(){var e=this;return!1===this.requestFinished?"":0===this.result.length?Object(u.jsx)(y,{}):Object(u.jsx)("div",{class:"container mt-5",children:Object(u.jsx)("div",{class:"row",children:Object(u.jsxs)("div",{class:"col-10 offset-1",children:[Object(u.jsxs)("h3",{children:["List of indexes ",Object(u.jsxs)("a",{href:"/gui/index/create",class:"btn btn-success btn-sm",children:[Object(u.jsx)("i",{class:"bi bi-plus"})," New"]})]}),Object(u.jsxs)("table",{class:"table",children:[Object(u.jsx)("thead",{children:Object(u.jsxs)("tr",{children:[Object(u.jsx)("th",{children:"Name"}),Object(u.jsx)("th",{children:"Paths"}),Object(u.jsx)("th",{children:"Ignore"}),Object(u.jsx)("th",{children:"Last indexed"}),Object(u.jsx)("th",{children:"\xa0"})]})}),Object(u.jsx)("tbody",{children:this.result.map((function(t){return Object(u.jsxs)("tr",{children:[Object(u.jsxs)("td",{children:[Object(u.jsx)("i",{class:"bi bi-folder2-open"})," ",t.name]}),Object(u.jsx)("td",{children:t.path.map((function(e){return Object(u.jsx)("small",{class:"badge bg-primary ms-1",children:e})}))}),Object(u.jsxs)("td",{children:[t.ignorePath.map((function(e){return Object(u.jsx)("small",{class:"badge bg-secondary ms-1",children:e})})),t.ignorePhrase.map((function(e){return Object(u.jsxs)("small",{class:"badge bg-secondary ms-1",children:["*",e,"*"]})})),t.ignoreExtension.map((function(e){return Object(u.jsxs)("small",{class:"badge bg-secondary ms-1",children:["*.",e]})}))]}),Object(u.jsxs)("td",{children:[new Date(t.lastStructureIndexed).toLocaleDateString(),"\xa0",new Date(t.lastStructureIndexed).toLocaleTimeString()]}),Object(u.jsxs)("td",{children:[Object(u.jsx)("a",{"data-placement":"top",title:"Edit",href:"/gui/index/edit/".concat(t.name),class:"btn btn-light btn-sm me-1",children:Object(u.jsx)("i",{class:"bi bi-pencil"})}),Object(u.jsx)("a",{"data-placement":"top",title:"Remove",onClick:function(){return e.handleRemove(t)},href:"#",class:"btn btn-danger btn-sm me-1",children:Object(u.jsx)("i",{class:"bi bi-trash"})}),Object(u.jsx)(g,{indextype:t.name})]})]})}))})]})]})})})}}]),n}(a.a.Component),C=function(e){Object(h.a)(n,e);var t=Object(j.a)(n);function n(e){var s;return Object(o.a)(this,n),(s=t.call(this,e)).state={formData:{extractMetadata:!0,ignoreExtension:[],ignorePath:[],ignorePhrase:[],indexMode:"full",intervalUpdateMetadata:3600,name:"",path:[]}},s.handleSubmit=s.handleSubmit.bind(Object(b.a)(s)),s.handleFormEdit=s.handleFormEdit.bind(Object(b.a)(s)),s}return Object(d.a)(n,[{key:"componentDidMount",value:function(){var e=this,t=this.props.match.params.name;void 0!==t&&fetch("".concat("","/index/get/").concat(t)).then((function(e){return e.json()})).then((function(t){e.setState({formData:t})}))}},{key:"handleSubmit",value:function(e){e.preventDefault(),fetch("".concat("","/index/set"),{method:"POST",body:JSON.stringify(this.state.formData),headers:{"Content-Type":"application/json"}}).then((function(e){console.log(e),window.location="/gui/index"}))}},{key:"handleFormEdit",value:function(e){if("change"===e.type){var t=this.state.formData;switch(e.target.id){case"inputName":t.name=e.target.value.replace(/[^a-z0-9\-]+/gi,"");break;case"inputPath":t.path=e.target.value.split(",");break;case"inputPathIgnore":t.ignorePath=e.target.value.split(",");break;case"inputPhraseIgnore":t.ignorePhrase=e.target.value.split(",");break;case"inputExtIgnore":t.ignoreExtension=e.target.value.split(",");break;case"inputUpdateStructure":t.intervalUpdateStructure=e.target.value.replace(/[^0-9]+/gi,"");break;case"inputExtractMetadata":t.extractMetadata=e.target.checked}this.setState({formData:t})}}},{key:"render",value:function(){var e=!0;return void 0===this.props.match.params.name&&(e=!1),Object(u.jsx)("div",{class:"container mt-5",children:Object(u.jsx)("div",{class:"row",children:Object(u.jsxs)("div",{class:"col-10 offset-1",children:[Object(u.jsxs)("h1",{children:[e?"Edit":"Create"," index"]}),Object(u.jsxs)("form",{onSubmit:this.handleSubmit,children:[Object(u.jsxs)("div",{class:"row mb-3",children:[Object(u.jsx)("label",{for:"inputName",class:"col-sm-3 col-form-label",children:"Index name"}),Object(u.jsx)("div",{class:"col-sm-5",children:Object(u.jsx)("input",{value:this.state.formData.name,onChange:this.handleFormEdit,type:"text",class:"form-control",id:"inputName"})}),Object(u.jsx)("div",{class:"col-sm-4"})]}),Object(u.jsxs)("div",{class:"row mb-3",children:[Object(u.jsx)("label",{for:"inputPath",class:"col-sm-3 col-form-label",children:"Path to index"}),Object(u.jsx)("div",{class:"col-sm-5",children:Object(u.jsx)("input",{value:this.state.formData.path.join(","),onChange:this.handleFormEdit,type:"text",class:"form-control",id:"inputPath"})}),Object(u.jsx)("div",{class:"col-sm-4",children:Object(u.jsxs)("small",{class:"text-muted",children:["Full path to index, you can add here multiple paths separate them with coma ",Object(u.jsx)("br",{}),"eg. (/home/user/dir1,/home/user/dir2)"]})})]}),Object(u.jsx)("h4",{children:"Filter settings"}),Object(u.jsxs)("div",{class:"row mb-3",children:[Object(u.jsx)("label",{for:"inputPathIgnore",class:"col-sm-3 col-form-label",children:"Ignore path"}),Object(u.jsx)("div",{class:"col-sm-5",children:Object(u.jsx)("input",{value:this.state.formData.ignorePath.join(","),onChange:this.handleFormEdit,type:"text",class:"form-control",id:"inputPathIgnore"})}),Object(u.jsx)("div",{class:"col-sm-4",children:Object(u.jsxs)("small",{class:"text-muted",children:["Ignore paths from indexing separated by coma ",Object(u.jsx)("br",{}),"eg. (/home/user/dir1/unwanted-path,/home/user/dir2/some/unwanted)"]})})]}),Object(u.jsxs)("div",{class:"row mb-3",children:[Object(u.jsx)("label",{for:"inputPhraseIgnore",class:"col-sm-3 col-form-label",children:"Ignore phrase"}),Object(u.jsx)("div",{class:"col-sm-5",children:Object(u.jsx)("input",{value:this.state.formData.ignorePhrase.join(","),onChange:this.handleFormEdit,type:"text",class:"form-control",id:"inputPhraseIgnore"})}),Object(u.jsx)("div",{class:"col-sm-4",children:Object(u.jsxs)("small",{class:"text-muted",children:["Ignore file/directory paths with phrase separated by coma. You can use * to match more files/directories ",Object(u.jsx)("br",{}),"eg. (.git,vendor,attr*)"]})})]}),Object(u.jsxs)("div",{class:"row mb-3",children:[Object(u.jsx)("label",{for:"inputExtIgnore",class:"col-sm-3 col-form-label",children:"Ignore extensions"}),Object(u.jsx)("div",{class:"col-sm-5",children:Object(u.jsx)("input",{value:this.state.formData.ignoreExtension.join(","),onChange:this.handleFormEdit,type:"text",class:"form-control",id:"inputExtIgnore"})}),Object(u.jsx)("div",{class:"col-sm-4",children:Object(u.jsxs)("small",{class:"text-muted",children:["Ignore file extensions separated by coma ",Object(u.jsx)("br",{}),"eg. (jpg,mp4,tmp)"]})})]}),Object(u.jsx)("h4",{children:"Reindex settings"}),Object(u.jsxs)("div",{class:"row mb-3",children:[Object(u.jsx)("label",{for:"inputUpdateStructure",class:"col-sm-3 col-form-label",children:"File structure interval"}),Object(u.jsx)("div",{class:"col-sm-5",children:Object(u.jsx)("input",{value:this.state.formData.intervalUpdateStructure,onChange:this.handleFormEdit,type:"text",class:"form-control",id:"inputUpdateStructure"})}),Object(u.jsx)("div",{class:"col-sm-4",children:Object(u.jsx)("small",{class:"text-muted",children:"Refresh directory structure interval in seconds."})})]}),Object(u.jsxs)("div",{class:"row mb-3",children:[Object(u.jsx)("label",{for:"inputExtractMetadata",class:"col-sm-3 col-form-label",children:"Extract metadata from files"}),Object(u.jsx)("div",{class:"col-sm-5",children:Object(u.jsx)("input",{onChange:this.handleFormEdit,checked:this.state.formData.extractMetadata,type:"checkbox",class:"form-check-input",id:"inputExtractMetadata"})}),Object(u.jsx)("div",{class:"col-sm-4",children:Object(u.jsx)("small",{class:"text-muted",children:"Enable this option when Filehunter should extract metadata/content from files and make it searchable"})})]}),Object(u.jsx)("div",{class:"row",children:Object(u.jsxs)("div",{class:"col-3 offset-6",children:[Object(u.jsx)("a",{href:"/gui/index",class:"btn btn-link",children:"Cancel"}),Object(u.jsx)("button",{type:"submit",class:"btn btn-primary float-right",children:"Save"})]})})]})]})})})}}]),n}(a.a.Component),S=Object(l.g)(C);var w=function(){return Object(u.jsxs)("div",{children:[Object(u.jsx)("header",{children:Object(u.jsx)(m,{})}),Object(u.jsx)("main",{class:"flex-shrink-2",children:Object(u.jsx)(r.a,{children:Object(u.jsxs)(l.d,{children:[Object(u.jsx)(l.b,{exact:!0,path:"/",children:Object(u.jsx)(l.a,{to:"/gui/"})}),Object(u.jsx)(l.b,{exact:!0,path:"/gui/",children:Object(u.jsx)(v,{})}),Object(u.jsx)(l.b,{exact:!0,path:"/gui/index",children:Object(u.jsx)(k,{})}),Object(u.jsx)(l.b,{path:"/gui/index/edit/:name",children:Object(u.jsx)(S,{})}),Object(u.jsx)(l.b,{path:"/gui/index/create",children:Object(u.jsx)(S,{})})]})})})]})},F=function(e){e&&e instanceof Function&&n.e(3).then(n.bind(null,41)).then((function(t){var n=t.getCLS,s=t.getFID,a=t.getFCP,c=t.getLCP,i=t.getTTFB;n(e),s(e),a(e),c(e),i(e)}))};n(37),n(36);i.a.render(Object(u.jsx)(a.a.StrictMode,{children:Object(u.jsx)(w,{})}),document.getElementById("root")),F()}},[[38,1,2]]]);
//# sourceMappingURL=main.6c323a16.chunk.js.map