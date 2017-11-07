 environments{
	//默认测试环境
	base{
		encoding = "utf-8"
		db{
			name = "appsugar_kotlin"
		}
		test{
			jdbc{
				groupId = "com.h2database"
				artifactId = "h2"
				version = "1.4.193"
				url = "jdbc:h2:~/tmp/${db.name}"
				driverClassName = "org.h2.Driver"
				username = "sa"
				password = ""
			}
			hibernate{
				dialect = "org.hibernate.dialect.H2Dialect"
			}
			dbunit{
				dataTypeFactoryName = "org.dbunit.ext.h2.H2DataTypeFactory"
				operationType = "CLEAN_INSERT"
				sampleData =  "/src/test/resources/sample-data.xml"
			}
		}
		
		jdbc = test.jdbc
		hibernate = test.hibernate
	}
	
	//本机测试环境
	mysql{
		db{
			name = "appsugar_kotlin"
		}
		jdbc {
			groupId = "mysql"
			artifactId = "mysql-connector-java"
			version = "5.1.39"
			url = "jdbc:mysql://localhost:3306/${db.name}?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8"
			driverClassName = "com.mysql.jdbc.Driver"
			username = "root"
			password = "123456"
		}
		hibernate{
			dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
		}
	}

}