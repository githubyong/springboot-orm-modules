import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

import javax.swing.*
import java.awt.Dialog
import java.lang.reflect.Method
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

config = [
        // 生成开关
        generate: [
                entity          : true,
                service         : true,
                repository      : true,
                repositoryCustom: false
        ],
        // 实体生成设置
        entity  : [
                // 继承父类设置
                parent         : [
                        // 是否继承父类
                        enable    : true,
                        // 父类名称
                        name      : "BaseEntity",
                        // 父类包名
                        package   : "org.example.sdj.advance.support.entity",
                        // 父类的属性，父类已有属性不在出现在生成的实体内
                        properties: ["version"],
                ],
                // 是否序列化
                impSerializable: true,
                //是否用spring-data-jdbc
                springdata           : true,
                // 是否生成 swagger 文档相关注解，相关说明来数据库注释
                useSwagger     : false,
                // 是否使用 lombok 注解代替 get、set方法
                useLombok      : true,
                jdbcTemplateName :"gecrisJdbcTemplate"
        ],
        // service 生成设置
        service : [
                // 参照 entity 部分的 parent
                parent: [
                        enable : false,
                        name   : "BaseService",
                        package: "com.marioplus.jpagendemo.common"
                ]
        ]
]

typeMapping = [
        (~/(?i)bool|boolean/)     : "Boolean",
        (~/(?i)bigint/)                   : "Long",
        (~/(?i)int|tinyint/)                      : "Integer",
        (~/(?i)float|double|decimal|real/): "BigDecimal",
        (~/(?i)datetime|timestamp|date|time/)               : "Date",
        (~/(?i)/)                         : "String"
]


FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter {
        it instanceof DasTable && it.getKind() == ObjectKind.TABLE
    }
            .each { table ->
                def fields = calcFields(table)
                Gen.main(config, table, fields, dir.toString())
            }
}

// 转换类型
def calcFields(table) {
    def pk = DasUtil.getPrimaryKey(table).getColumnsRef().iterate().next()

//    JOptionPane.showMessageDialog(null, pk, "\u6807\u9898", JOptionPane.PLAIN_MESSAGE)
    DasUtil.getColumns(table).reduce([]) { fields, col ->
//        console(col, i++)
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        fields += [[
                           name        : Utils.toCamelCase(col.getName().toLowerCase().toString()),
                           column      : col.getName(),
                           type        : typeStr,
                           dataType    : Utils.firstMatched(col.getDataType(), /\b\w+\b/, ""),
                           len         : Utils.firstMatched(col.getDataType(), /(?<=\()\d+(?!=\))/, -1),
                           default     : col.getDefault(),
                           comment     : col.getComment(),
                           nullable    : !col.isNotNull(),
                           isPrimaryKey: null != pk && col.getName() == pk,
                   ]]
    }
}

class Gen {

    // 生成对应的文件
    def static main(config, table, fields, dir) {

        def entityName = Utils.toUpperCamelCase(table.getName())
        def doName = "${entityName}";
        def basePackage = Utils.firstMatched(dir.toString(), /(?<=src\\main\\java\\).+/, "").replace("\\", ".")
        dir = dir.toString()
        def pkType = fields.find { it.isPrimaryKey }.type

        // entity
        if (config.generate.entity) {
            Utils.createFile("${dir}\\entity", "${entityName}.java").withWriter("utf8") {
                writer -> genEntity(writer, config, config.entity.parent, table, entityName,doName, fields, basePackage)
            }
        }
        // service
        if (config.generate.service) {
            Utils.createFile("${dir}\\service", "${entityName}Service.java").withWriter("utf8") {
                writer -> genService(writer, config, config.service.parent, entityName, pkType, basePackage)
            }
        }

        // rep
        if (config.generate.repository) {
            Utils.createFile("${dir}\\dao", "${entityName}Repository.java").withWriter("utf8") {
                writer -> genRepository(writer, config, entityName, doName,basePackage, pkType)
            }
        }

        // repCustom
        if (config.generate.repositoryCustom) {
            Utils.createFile("${dir}\\repository", "${entityName}RepositoryCustom.java").withWriter("utf8") {
                writer -> genRepositoryCustom(writer, entityName,basePackage)
            }
            Utils.createFile("${dir}\\repository\\impl", "${entityName}RepositoryCustomImpl.java").withWriter("utf8") {
                writer -> genRepositoryCustomImpl(writer, entityName, basePackage,config.entity.jdbcTemplateName)
            }
        }

    }

    // 生成实体
    def static genEntity(writer, config, parentConfig, table, entityName,doName, fieldList, basePackage) {

        writer.writeLine "package ${basePackage}.entity;"
        writer.writeLine ""
        if (config.entity.useSwagger) {
            writer.writeLine "import io.swagger.annotations.ApiModel;"
            writer.writeLine "import io.swagger.annotations.ApiModelProperty;"
        }
        if (parentConfig.enable) {
            writer.writeLine "import ${parentConfig.package}.${parentConfig.name};"
        }
        if (config.entity.springdata) {
            writer.writeLine "import org.springframework.data.annotation.Id;"
            writer.writeLine "import org.springframework.data.relational.core.mapping.Table;"
            writer.writeLine "import org.springframework.data.relational.core.mapping.Column;"
        }

        if (config.entity.useLombok) {
            writer.writeLine "import lombok.Data;"
            writer.writeLine "import lombok.Builder;"
            writer.writeLine "import lombok.AllArgsConstructor;"
            writer.writeLine "import lombok.NoArgsConstructor;"
            writer.writeLine ""
        }
        if (config.entity.impSerializable) {
            writer.writeLine "import java.io.Serializable;"
            writer.writeLine ""
        }

        Set types = new HashSet()
        fieldList.each() {
            types.add(it.type)
        }

        if (types.contains("BigDecimal")) {
            writer.writeLine "import java.math.BigDecimal;"
        }
        if (types.contains("Date")) {
            writer.writeLine "import java.util.Date;"
        }

        def tableComment = Utils.getDefaultValIfCurrentValIsBlank(table.getComment(), entityName)
        writer.writeLine ""
        writer.writeLine "/**"
        writer.writeLine " * $tableComment"
        writer.writeLine " *"
        writer.writeLine " * @author auto generated"
        writer.writeLine " * @date ${Utils.localDateTimeStr()}"
        writer.writeLine " */"
        if (config.entity.useLombok) {
            writer.writeLine "@Data"
            writer.writeLine "@Builder"
            writer.writeLine "@NoArgsConstructor"
            writer.writeLine "@AllArgsConstructor"
        }
        if (config.entity.springdata) {
            writer.writeLine "@Table(\"${table.name}\")"
        }
        if (config.entity.useSwagger) {
            writer.writeLine "@ApiModel(value = \"${tableComment}\")"
        }

        def extendsStr = parentConfig.enable ? " extends $parentConfig.name" : "",
            impStr = config.entity.impSerializable ? " implements Serializable" : ""

        writer.writeLine "public class ${doName}$extendsStr$impStr {"

        if (parentConfig.enable) {
            fieldList = fieldList.findAll { field -> !parentConfig.properties.contains(field.name) }
        }

        fieldList.each() { field -> genEntityProperties(writer, config, parentConfig, field) }

        if (!config.entity.useLombok) {
            fieldList.each() { field -> genEntityGetAndSetMethod(writer, field) }
        }
        writer.writeLine "}"
    }

    // 实体属性
    def static genEntityProperties(writer, config, parentConfig, field) {
        writer.writeLine ""
        def comment = Utils.getDefaultValIfCurrentValIsBlank(field.comment, field.name)

        if (field.isPrimaryKey && config.entity.springdata) {
            writer.writeLine "\t@Id"
        }
        if (config.entity.useSwagger) {
            writer.writeLine "\t@ApiModelProperty(value = \"${comment}\")"
        }

        if (config.entity.springdata) {
            writer.writeLine "\t@Column(\"${field.column}\")"
        }
        writer.writeLine "\tprivate ${field.type} ${field.name};"
    }

    // 生成get、get方法
    def static genEntityGetAndSetMethod(writer, field) {

        def methodName = Utils.toUpperCamelCase(field.name)

        // get
        writer.writeLine "\t"
        writer.writeLine "\tpublic ${field.type} get${methodName}() {"
        writer.writeLine "\t\treturn this.${field.name};"
        writer.writeLine "\t}"

        // set
        writer.writeLine "\t"
        writer.writeLine "\tpublic void set${methodName}($field.type $field.name) {"
        writer.writeLine "\t\tthis.${field.name} = ${field.name};"
        writer.writeLine "\t}"
    }

    // 生成Service
    def static genService(writer, config, parentConfig, entityName, pkType, basePackage) {
        def repName = Utils.toCamelCase(entityName+"Repository".toString())
        writer.writeLine "package ${basePackage}.service;"
        writer.writeLine ""
        writer.writeLine "import ${basePackage}.dao.${entityName}Repository;"
        if (parentConfig.enable) {
            writer.writeLine "import $parentConfig.package.$parentConfig.name;"
            writer.writeLine "import ${basePackage}.entity.$entityName;"
        }
        writer.writeLine "import org.springframework.stereotype.Service;"
        writer.writeLine ""
        writer.writeLine "import javax.annotation.Resource;"
        writer.writeLine ""
        writer.writeLine "/**"
        writer.writeLine " * $entityName service\u5c42"
        writer.writeLine " *"
        writer.writeLine " * @author auto generated"
        writer.writeLine " * @date ${Utils.localDateTimeStr()}"
        writer.writeLine " */"
        writer.writeLine "@Service"

        def extendsStr = parentConfig.enable ? " extends ${parentConfig.name}<$entityName, $pkType>" : ""
        writer.writeLine "public class ${entityName}Service${extendsStr} {"
        writer.writeLine ""
        writer.writeLine "\t@Resource"
        writer.writeLine "\tprivate ${entityName}Repository ${repName};"
        writer.writeLine "}"
    }

    // 生成rep
    def static genRepository(writer, config, entityName,doName, basePackage, pkType) {
        def customStr = config.generate.repositoryCustom ? ", ${entityName}RepositoryCustom" : ""

        writer.writeLine "package ${basePackage}.dao;"
        writer.writeLine ""
        writer.writeLine "import ${basePackage}.entity.$entityName;"
        writer.writeLine "import org.springframework.data.repository.PagingAndSortingRepository;"
        writer.writeLine ""
        writer.writeLine "/**"
        writer.writeLine " * $entityName Repository"
        writer.writeLine " *"
        writer.writeLine " * @author auto generated"
        writer.writeLine " * @date ${Utils.localDateTimeStr()}"
        writer.writeLine " */"
        writer.writeLine "public interface ${entityName}Repository extends PagingAndSortingRepository<$entityName, $pkType>$customStr {"
        writer.writeLine ""
        writer.writeLine "}"
    }

    // 生成repCustom
    def static genRepositoryCustom(writer, entityName, basePackage) {
        writer.writeLine "package ${basePackage}.dao;"
        writer.writeLine ""
        writer.writeLine "/**"
        writer.writeLine " * $entityName Custom Repository"
        writer.writeLine " *"
        writer.writeLine " * @author auto generated"
        writer.writeLine " * @date ${Utils.localDateTimeStr()}"
        writer.writeLine " */"
        writer.writeLine "public interface ${entityName}RepositoryCustom {"
        writer.writeLine ""
        writer.writeLine "}"
    }

    // 生成repCustomImp
    def static genRepositoryCustomImpl(writer, entityName, basePackage,jdbcTemplateName) {
        writer.writeLine "package ${basePackage}.dao.impl;"
        writer.writeLine ""
        writer.writeLine "import ${basePackage}.dao.${entityName}RepositoryCustom;"
        writer.writeLine "import org.springframework.stereotype.Repository;"
        writer.writeLine ""
        writer.writeLine "import org.springframework.beans.factory.annotation.Autowired;"
        writer.writeLine "import org.springframework.jdbc.core.JdbcTemplate;"
        writer.writeLine ""
        writer.writeLine "/**"
        writer.writeLine " * $entityName Custom Repository"
        writer.writeLine " *"
        writer.writeLine " * @author auto generated"
        writer.writeLine " * @date ${Utils.localDateTimeStr()}"
        writer.writeLine " */"
        writer.writeLine "@Repository"
        writer.writeLine "public class ${entityName}RepositoryCustomImpl implements ${entityName}RepositoryCustom {"
        writer.writeLine ""
        writer.writeLine "\t@Autowired"
        writer.writeLine "\tprivate JdbcTemplate ${jdbcTemplateName};"
        writer.writeLine "}"
    }

}

class Utils {

    /**
     * 提示框
     * @param message
     * @return
     */
    static def dialog(message) {
        JOptionPane.showMessageDialog(null, message, "\u6807\u9898", JOptionPane.PLAIN_MESSAGE)
    }

    /**
     * 反射获取主键列名，
     * @param table
     * @return 若没有返回null
     */
    static def getPK(table) {
        def method = table.getClass().getMethod("getText")
        method.setAccessible(true)
        def text = method.invoke(table).toString()
        def reg = /(?<=\s{4,})\b[^\s]+\b(?!=.+\n\s+PRIMARY KEY,)/
        firstMatched(text, reg, null)
    }

    /**
     *  转换为大写驼峰
     * @param content
     * @return
     */
    static def toUpperCamelCase(content) {
        content.toString()
                .split(/_/)
                .toList()
                .stream()
                .filter { s -> s.length() > 0 }
                .map { s -> s.replaceFirst("^.", s.substring(0, 1).toUpperCase()) }
                .collect(Collectors.joining())
    }

    /**
     *  转换为驼峰
     * @param content
     * @return
     */
    static def toCamelCase(content) {
        content = content.toString()
        toUpperCamelCase(content).replaceFirst(/^../, content.substring(0, 2).toLowerCase())
    }

    /**
     * 寻找第一个匹配的值
     * @param content 匹配内容
     * @param reg 正则
     * @param defaultValue 默认值
     * @return 根据正则匹配，能匹配就返回匹配的值，不能则匹配默认值
     */
    static def firstMatched(content, reg, defaultValue) {
        if (null == content) {
            return defaultValue
        }
        def m = content =~ reg
        if (m.find()) {
            return m.group()
        }
        return defaultValue
    }

    static def localDateTimeStr() {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    static def createFile(filePath, fileName) {
        def file = new File(filePath)

        if (!file.exists()) {
            file.mkdir()
        }

        file = new File(filePath + "/" + fileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    static def getDefaultValIfCurrentValIsBlank(currentVal, defaultVal) {
        if (null == currentVal || currentVal.isEmpty()) {
            return defaultVal
        }
        return currentVal
    }
}

class Debug {

    // 反射获取所有方法
    static def console(col) {
        def clazz = col.getClass()
        def desktop = "C:/Users/mario/Desktop/"
        def fileName = "console${col.getName()}.txt"
        def path = desktop + fileName

        def file = new File(path)
        if (!file.exists()) {
            file.createNewFile()
        }

        file.withWriter("utf8") { writer ->
            writer.writeLine "base properties:"
            writer.writeLine "name\t${clazz.name}"
            writer.writeLine "simpleName\t${clazz.simpleName}"
            writer.writeLine ""

            writer.writeLine "public methods:"
            HashSet<Method> objMethodSet = new HashSet<Method>(new Object().getClass().methods as Collection<? extends Method>)
            HashSet<Method> colMethodSet = new HashSet<Method>(clazz.methods as Collection<? extends Method>)

            colMethodSet.stream()
                    .filter { m -> !objMethodSet.contains(m) && m.name.matches("^(get|is).+") && m.parameterCount == 0 }
                    .sorted { m1, m2 -> (m1.name <=> m2.name) }
                    .forEach { m ->
                        m.setAccessible(true)
                        writer.writeLine "method name:\t${m.name}"
                        writer.writeLine "invoke result:\t${m.invoke(col)}"
                        writer.writeLine ""
                    }

            writer.writeLine "all methods:"
            Arrays.stream(clazz.declaredMethods)
                    .sorted { m1, m2 -> (m1.name <=> m2.name) }
                    .forEach {
                        writer.writeLine "#"
                        writer.writeLine "name\t${it.name}"
                        writer.writeLine "accessible\t${it.accessible.toString()}"
                        writer.writeLine "return\t${it.returnType.name}"
                        writer.writeLine "paramCount\t${it.parameterCount}"
                        writer.write "param\t"
                        it.parameterTypes.each { writer.write "${it.name}\t" }
                        writer.write "param\t"
                        it.parameters.each { writer.write "${it.name}\t" }

                        writer.writeLine ""
                    }

            writer.writeLine ""
        }
    }

    // 测试获取的字段
    static def consoleFields(fields) {
        def desktop = "C:/Users/mario/Desktop/"
        def fileName = "console-fields.txt"
        def path = desktop + fileName

        def file = new File(path)
        if (!file.exists()) {
            file.createNewFile()
        }

        file.withWriter("utf8") { writer ->
            fields.each {
                it.each { k, v ->
                    writer.writeLine "${k}:\t${v}"
                }
                writer.writeLine "======================================"
            }
        }
    }
}