export default {
	name:"PermissionUtil",
	storePermission(permissionArray){
		localStorage.permission = permissionArray
	},
	hasPermission(permission){
		if(!permission)return true
		var permissions = localStorage.permission
		if(!permissions)return false;//一直返回可用
		return permissions.indexOf(permission) != -1
	},
	anyOfChildPermission(childrenPermissions){
		for(var i =0;i<childrenPermissions.length;i++){
			var permission = childrenPermissions[i].permission;
			if(this.hasPermission(permission))return true
		}
	}
}