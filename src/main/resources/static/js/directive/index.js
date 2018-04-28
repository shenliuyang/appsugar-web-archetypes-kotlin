var saas__directive__vue = [
	{
		name: 'errorImage',
		inserted: function (el, binding) {
			var errSrc = binding.value || '/images/errImage.png';
			el.onerror = function () {
				el.src = errSrc;
			}
		}
	}
];

// 遍历所有指令，设置为vue的全局指令
saas__directive__vue.map(function(directive) {
	Vue.directive(directive.name, directive);
});
