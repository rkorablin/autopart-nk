$(document).ready(function() {
	var query = $.query.get("query").replace("\.", "").replace("\\", "").replace("-", "").replace(" ", "").replace("/", "");
	$(".category-products-table tbody").highlightRegex(new RegExp(query, "ig"));
	var newQuery = query.split("").join("[- \.\\/]?");
	$(".category-products-table tbody .td-code").highlightRegex(new RegExp(newQuery, "ig"));
});