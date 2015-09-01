$(document).ready(function() {
	var $csrf = $("#csrf");
	$(".admin-settings-table td.value a").editable({
		url: "/admin/set_setting_value?" + $csrf.attr("name") + "=" + $csrf.val()
	});
});