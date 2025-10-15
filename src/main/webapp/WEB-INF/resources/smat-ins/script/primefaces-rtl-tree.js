/**
 * 
 */
function reverseTreeChildExpandedIcon() {
  if (document.dir == "rtl"){
	$('span.ui-tree-node-active').parent().parent().find(
			'ul.ui-treenode-children li.ui-treenode-parent div span.ui-tree-toggler').removeClass(
			'ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-w');
			}
}

$(document).delegate('li.ui-treenode-parent', 'click', function(e) {
   if($('li.ui-treenode-parent div.ui-treenode-content').find('span.ui-tree-node-active'))
    $('li.ui-treenode-parent div.ui-treenode-content').find('span.ui-tree-node-active').removeClass('ui-tree-node-active');
	$(e.target).addClass('ui-tree-node-active');
});