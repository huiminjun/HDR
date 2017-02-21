var app = angular
		.module(
				'projectForm',
				[ 'ngRoute' ],
				function($httpProvider) {// ngRoute引入路由依赖
					$httpProvider.defaults.headers.put['Content-Type'] = 'application/x-www-form-urlencoded';
					$httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

					// Override $http service's default transformRequest
					$httpProvider.defaults.transformRequest = [ function(data) {
						/**
						 * The workhorse; converts an object to
						 * x-www-form-urlencoded serialization.
						 * 
						 * @param {Object}
						 *            obj
						 * @return {String}
						 */
						var param = function(obj) {
							var query = '';
							var name, value, fullSubName, subName, subValue, innerObj, i;

							for (name in obj) {
								value = obj[name];

								if (value instanceof Array) {
									for (i = 0; i < value.length; ++i) {
										subValue = value[i];
										fullSubName = name + '[' + i + ']';
										innerObj = {};
										innerObj[fullSubName] = subValue;
										query += param(innerObj) + '&';
									}
								} else if (value instanceof Object) {
									for (subName in value) {
										subValue = value[subName];
										fullSubName = name + '[' + subName
												+ ']';
										innerObj = {};
										innerObj[fullSubName] = subValue;
										query += param(innerObj) + '&';
									}
								} else if (value !== undefined
										&& value !== null) {
									query += encodeURIComponent(name) + '='
											+ encodeURIComponent(value) + '&';
								}
							}

							return query.length ? query.substr(0,
									query.length - 1) : query;
						};

						return angular.isObject(data)
								&& String(data) !== '[object File]' ? param(data)
								: data;
					} ];
				});
app.run([ '$rootScope', '$location', function($rootScope, $location) {
	$rootScope.$on('$routeChangeSuccess', function(evt, next, previous) {
		console.log('路由跳转成功');
		$rootScope.$broadcast('reGetData');
	});
} ]);

// 路由配置
app.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/proWorkLoadForm', {
		templateUrl : '/HDR/jsp/projectForm/proWorkloadForm.html',
		controller : 'ReportController'
	}).when('/proWorkLoadAnalyse', {
		templateUrl : '/HDR/jsp/projectForm/proWorkloadAnalyse.html',
		controller : 'ReportController'
	}).when('/proMaintainForm', {
		templateUrl : '/HDR/jsp/projectForm/proMaintainForm.html',
		controller : 'ReportController'
	}).when('/proMaintainAnalyse', {
		templateUrl : '/HDR/jsp/projectForm/proMaintainAnalyse.html',
		controller : 'ReportController'
	}).when('/proMaterialForm', {
		templateUrl : '/HDR/jsp/projectForm/proMaterialForm.html',
		controller : 'ReportController'
	})
} ]);

app.constant('baseUrl', '/HDR/');
app.factory('services', [ '$http', 'baseUrl', function($http, baseUrl) {
	var services = {};
	// zq获取房间类型列表
	services.getRoomSorts = function(data) {
		return $http({
			method : 'post',
			url : baseUrl + 'reportForm/getRoomSorts.do',
			data : data
		});
	};
	// zq获取全体成员
	services.selectRoomStaffs = function(data) {
		return $http({
			method : 'post',
			url : baseUrl + 'reportForm/selectRoomStaffs.do',
			data : data
		});
	};
	services.selectProWorkLoad = function(data) {
		return $http({
			method : 'post',
			url : baseUrl + 'projectWorkLoad/selectProWorkLoad.do',
			data : data
		});
	};
	services.selectProWorkLoadAnalyse == function(data) {
		return $http({
			method : 'post',
			url : baseUrl + 'projectWorkLoad/selectProWorkLoadAnalyse.do',
			data : data
		});
	};
	return services;
} ]);
app
		.controller(
				'ReportController',
				[
						'$scope',
						'services',
						'$location',
						function($scope, services, $location) {
							var reportForm = $scope;
							// zq打扫类型默认值
							reportForm.cleanTypes = [ {
								id : 0,
								type : "抹尘房"
							}, {
								id : 1,
								type : "离退房"
							}, {
								id : 2,
								type : "过夜房"
							} ];
							// zq查找时间季度默认值
							reportForm.quarters = [ {
								id : 0,
								type : "全年"
							}, {
								id : 1,
								type : "一季度"
							}, {
								id : 2,
								type : "二季度"
							}, {
								id : 3,
								type : "三季度"
							}, {
								id : 4,
								type : "四季度"
							} ];

							/**
							 * zq公共函数始
							 */
							function preventDefault(e) {
								if (e && e.preventDefault) {
									// 阻止默认浏览器动作(W3C)
									e.preventDefault();
								} else {
									// IE中阻止函数器默认动作的方式
									window.event.returnValue = false;
									return false;
								}
							}
							// zq为生成图表拼data
							function combine(da, name, arr) {
								var ss = new Object();
								ss.name = name;
								ss.data = arr;
								da.push(ss);
							}
							// zq生成平均值的数组
							function getAverageData(data, number) {
								var arr = [];
								for (var i = 0; i < number; i++) {
									arr.push(data);
								}
								return arr;

							}

							// zq换页
							function pageTurn(totalPage, page, Func) {
								var $pages = $(".tcdPageCode");
								if ($pages.length != 0) {
									$(".tcdPageCode").createPage({
										pageCount : totalPage,
										current : page,
										backFn : function(p) {
											Func(p);
										}
									});
								}
							}
							// zq折线图公用函数
							function lineChartForm(data, elementId, title,
									lx_Axis, ly_title) {
								var chart1 = new LineChart({
									elementId : elementId,
									title : title,
									data : data,
									lx_Axis : lx_Axis,
									ly_title : ly_title,
									subTitle : ''
								});
								chart1.init();
							}
							// zq扇形图公用函数
							function pieChartForm(elementId, title, dataName,
									data) {
								var chart1 = new Chart({
									elementId : elementId,
									title : title,
									data : data,
									name : dataName
								});
								chart1.init();
							}
							// zq获取房间类型下拉表
							function selectRoomSorts() {
								services.getRoomSorts().success(function(data) {
									reportForm.roomTypes = data.list;
								});
							}
							// zq查询客服人员列表
							function selectRoomStaffs(deptType) {
								services.selectRoomStaffs({
									deptType : deptType
								}).success(function(data) {
									reportForm.staffs = data.list;
								});
							}
							// zq获取所选房间类型
							function getSelectedRoomType(roomSortNo) {
								var type = "";
								for ( var item in reportForm.roomTypes) {
									if (reportForm.roomTypes[item].sortNo == roomSortNo) {
										type = reportForm.roomTypes[item].sortName;
									}
								}
								return type;
							}
							// zq获取所选打扫类型
							function getSelectedCleanType(cleanTypeId) {
								var type = "";
								switch (cleanTypeId) {
								case '0':
									type = "抹尘房";
									break;
								case '1':
									type = "离退房";
									break;
								case '2':
									type = "过夜房";
									break;
								}
								return type;
							}
							// zq获取所选用户
							function getSelectedStaff(staffId) {
								var staffName = "";
								for ( var item in reportForm.staffs) {
									if (reportForm.staffs[item].staff_id == staffId) {
										staffName = reportForm.staffs[item].staff_no
												+ reportForm.staffs[item].staff_name;
									}
								}
								return staffName;
							}
							// zq比较两个时间的大小
							function compareDateTime(startDate, endDate) {
								var date1 = new Date(startDate);
								var date2 = new Date(endDate);
								if (date2.getTime() < date1.getTime()) {
									return true;
								} else {
									return false;
								}
							}
							// zq当房型下拉框变化时获取房型名字
							reportForm.getSortNameByNo = function() {
								var no = $("#roomSortType").val();
								reportForm.sortName = getSelectedRoomType(no);
							}
							// zq将小数保留两位小数
							function changeNumType(number) {
								if (!number) {
									var defaultNum = 0;
									var num = parseFloat(parseFloat(defaultNum)
											.toFixed(2));
								} else {
									var num = parseFloat(parseFloat(number)
											.toFixed(2));
								}
								return num;
							}
							// zq获取下拉框得到的员工姓名
							reportForm.staffName = "";
							reportForm.getStaffNameById = function() {
								var name = $("#staffId").val();
								reportForm.staffName = getSelectedStaff(name);
							}
							function getSelectedQuarter(id) {
								var qName = "";
								switch (id) {
								case '0':
									qName = "全年";
									break;
								case '1':
									qName = "第一季度";
									break;
								case '2':
									qName = "第二季度";
									break;
								case '3':
									qName = "第三季度";
									break;
								case '4':
									qName = "第四季度";
									break;
								}
								return qName;
							}
							/**
							 * zq公共函数终
							 */
							reportForm.pwLimit = {
								startTime : "",
								endTime : ""
							};
							// zq添加员工工作量统计表
							reportForm.selectProWorkLoad = function() {
								if (reportForm.pwLimit.startTime == "") {
									alert("请选择起始时间！");
									return false;
								}
								if (reportForm.pwLimit.endTime == "") {
									alert("请选择截止时间！");
									return false;
								}
								$(".overlayer").fadeIn(200);
								$(".tipLoading").fadeIn(200);
								var proWorkLoadLimit = JSON
										.stringify(reportForm.pwLimit);
								services.selectProWorkLoad({
									limit : proWorkLoadLimit
								}).success(function(data) {
									$(".overlayer").fadeOut(200);
									$(".tipLoading").fadeOut(200);
									reportForm.workloadList = data.list;
									if (data.list) {
										reportForm.listIsShow = false;
									} else {
										reportForm.listIsShow = true;
									}
								});
							}
							reportForm.pwaLimit = {
								checkYear : '',
								quarter : '0',
								staffId : ''
							};
							// zq获取工程部工作量折线图分析
							reportForm.selectProWorkLoadAnalyse = function() {
								if (reportForm.pwaLimit.checkYear == '') {
									alert("请选择查询年份！");
									return false;
								}
								if (reportForm.pwaLimit.staffId == '') {
									alert("请选择查询员工！");
									return false;
								}
								$(".overlayer").fadeIn(200);
								$(".tipLoading").fadeIn(200);
								var pwaLimits = JSON
										.stringify(reportForm.pwaLimit);
								services
										.selectProWorkLoadAnalyse({
											limit : pwaLimits
										})
										.success(
												function(data) {
													$(".overlayer")
															.fadeOut(200);
													$(".tipLoading").fadeOut(
															200);
													var title = "工程部员工（"
															+ getSelectedStaff(reportForm.pwaLimit.staffId)
															+ "）"
															+ reportForm.pwaLimit.checkYear
															+ "年度  "
															+ getSelectedQuarter(reportForm.pwaLimit.quarter)
															+ "  工作量汇总表";// 折线图标题显示
													var xAxis = [];// 横坐标显示
													var yAxis = "工作量";// 纵坐标显示
													var nowQuarter = reportForm.pwaLimit.quarter;// 当前的选择季度
													var lineName = getSelectedStaff(reportForm.pwaLimit.staffId)
															+ "员工工作量";
													var lineData = [];// 最终传入chart1中的data
													var allAverageData = [];// 全体员工平均工作量的平均Data
													var averageData = [];// 个人平均工作量
													var userData = [];// 个人工作量
													for ( var item in data.list) {
														userData
																.push(changeNumType(data.list[item]));
													}
													switch (nowQuarter) {
													case '0':
														xAxis = [ '1月', '2月',
																'3月', '4月',
																'5月', '6月',
																'7月', '8月',
																'9月', '10月',
																'11月', '12月' ];
														allAverageData = getAverageData(
																changeNumType(data.allAverWorkLoad),
																12);
														averageData = getAverageData(
																changeNumType(data.averWorkLoad),
																12);

														break;
													case '1':
														xAxis = [ '1月', '2月',
																'3月' ];
														allAverageData = getAverageData(
																changeNumType(data.allAverWorkLoad),
																3);
														averageData = getAverageData(
																changeNumType(data.averWorkLoad),
																3);

														break;
													case '2':
														xAxis = [ '4月', '5月',
																'6月' ];
														allAverageData = getAverageData(
																changeNumType(data.allAverWorkLoad),
																3);
														averageData = getAverageData(
																data.averWorkLoad,
																3);
														break;
													case '3':
														xAxis = [ '7月', '8月',
																'9月' ];
														allAverageData = getAverageData(
																changeNumType(data.allAverWorkLoad),
																3);
														averageData = getAverageData(
																changeNumType(data.averWorkLoad),
																3);
														break;
													case '4':
														xAxis = [ '10月', '11月',
																'12月' ];
														allAverageData = getAverageData(
																changeNumType(data.allAverWorkLoad),
																3);
														averageData = getAverageData(
																changeNumType(data.averWorkLoad),
																3);
														break;
													}

													combine(lineData,
															"个人平均工作量",
															averageData);
													combine(lineData,
															"全体平均工作量",
															allAverageData);
													combine(lineData, lineName,
															userData);

													lineChartForm(lineData,
															"#lineChart",
															title, xAxis, yAxis);

													$('#chart-svg')
															.val(
																	$(
																			"#lineChart")
																			.highcharts()
																			.getSVG());
													if (data.analyseResult) {
														reportForm.listRemark = true;
														reportForm.remark = data.analyseResult;
														$("#analyseResult")
																.val(
																		data.analyseResult);
													} else {
														reportForm.listRemark = false;
														reportForm.remark = "";
														$("#analyseResult")
																.val("");
													}
												});
							};
							// zq工程部维修项报表统计
							reportForm.pmLimit = {
								startTime : '',
								endTime : ''
							}
							reportForm.selectProMaintain = function() {
								if (reportForm.pmLimit.startTime == '') {
									alert("请选择起始时间！");
									return false;
								}
								if (reportForm.pmLimit.endTime == '') {
									alert("请选择截止时间！");
									return false;
								}
								$(".overlayer").fadeIn(200);
								$(".tipLoading").fadeIn(200);
								proMaintainLimit = JSON
										.stringify(reportForm.pmLimit);
								services
										.selectProMaintain({
											limit : proMaintainLimit
										})
										.success(
												function(data) {
													reportForm.toiletList = data.toiletList;
													reportForm.lockerList = data.lockerList;
													reportForm.barList = data.barList;
													reportForm.bedRoomList = data.bedRoomList;
													reportForm.airConditionerList = data.airConditionerList;
													reportForm.carpetList = data.carpetList;
													reportForm.windowList = data.windowList;
													reportForm.doorList = data.doorList;
													reportForm.publicList = data.publicList;
													if (data.list) {
														reportForm.listIsShow = false;
													} else {
														reportForm.listIsShow = true;
													}
												});
							}
							// 显示隐藏表格
							reportForm.showOrHide = {
								toiletIssue : false,
								lockerIssue : false,
								barIssue : false,
								bedRoomIssue : false,
								airConditionerIssue : false,
								carpetIssue : false,
								windowIssue : false,
								doorIssue : false,
								publicIssue : false
							};
							reportForm.showContInfo = function(target) {

								changeFalseToTrue(target.name);
							};
							reportForm.hideContInfo = function(target) {

								changeTrueTofalse(target.name)
							}
							function changeFalseToTrue(data) {
								switch (data) {
								case 'toiletIssue':
									reportForm.showOrHide.toiletIssue = true;
									break;
								case 'lockerIssue':
									reportForm.showOrHide.lockerIssue = true;
									break;
								case 'barIssue':
									reportForm.showOrHide.barIssue = true;
									break;
								case 'bedRoomIssue':
									reportForm.showOrHide.bedRoomIssue = true;
									break;
								case 'airConditionerIssue':
									reportForm.showOrHide.airConditionerIssue = true;
									break;
								case 'carpetIssue':
									reportForm.showOrHide.carpetIssue = true;
									break;
								case 'windowIssue':
									reportForm.showOrHide.windowIssue = true;
									break;
								case 'doorIssue':
									reportForm.showOrHide.doorIssue = true;
									break;
								case 'publicIssue':
									reportForm.showOrHide.publicIssue = true;
									break;
								}

							}
							function changeTrueTofalse(data) {
								switch (data) {
								case 'toiletIssue':
									reportForm.showOrHide.toiletIssue = false;
									break;
								case 'lockerIssue':
									reportForm.showOrHide.lockerIssue = false;
									break;
								case 'barIssue':
									reportForm.showOrHide.barIssue = false;
									break;
								case 'bedRoomIssue':
									reportForm.showOrHide.bedRoomIssue = false;
									break;
								case 'airConditionerIssue':
									reportForm.showOrHide.airConditionerIssue = false;
									break;
								case 'carpetIssue':
									reportForm.showOrHide.carpetIssue = false;
									break;
								case 'windowIssue':
									reportForm.showOrHide.windowIssue = false;
									break;
								case 'doorIssue':
									reportForm.showOrHide.doorIssue = false;
									break;
								case 'publicIssue':
									reportForm.showOrHide.publicIssue = false;
									break;
								}

							}
							// zq初始化
							function initData() {
								console.log("初始化页面信息");
								Highcharts
										.wrap(
												Highcharts.Chart.prototype,
												'getSVG',
												function(proceed) {
													return proceed
															.call(this)
															.replace(
																	/(fill|stroke)="rgba([ 0-9]+,[ 0-9]+,[ 0-9]+),([ 0-9\.]+)"/g,
																	'$1="rgb($2)" $1-opacity="$3"');
												});
								if ($location.path().indexOf(
										'/proWorkLoadAnalyse') == 0) {
									selectRoomStaffs(1);

								}
							}
							initData();
							// zq控制年
							var $dateFormat = $(".dateFormatForY");
							var dateRegexpForY = /^[0-9]{4}$/;
							$(".dateFormatForY").blur(
									function() {
										if (this.value.trim() != "") {
											if (!dateRegexpForY
													.test(this.value)) {
												$(this).parent().children(
														"span").css('display',
														'inline');
											} else {
												var month = parseInt(this.value
														.split("-")[1]);
												if (month > 12) {
													$(this).parent().children(
															"span")
															.css('display',
																	'inline');
												}
											}
										}
									});
							$(".dateFormatForY").click(
									function() {
										$(this).parent().children("span").css(
												'display', 'none');
									});

						} ]);

// 小数过滤器
app.filter('numFloat', function() {
	return function(input) {
		if (!input) {
			var number = parseFloat('0').toFixed(2);
		} else {
			var number = parseFloat(input).toFixed(2);
		}
		return number;
	}
});