package iwh.com.simplewen.win0.ybconsole.activity.modal

/**
 * 轻应用数据bean
 * @param itemStatus 应用状态(通过审核：运营状态）：审核与开发
 * @param itemLevel 权限等级
 * @param itemLogoUrl logo链接
 * @param itemUrl 应用详情页
 * @param itemName 名字
 */
data class LightItem(val itemName:String,val itemLogoUrl:String,val itemLevel:String,val itemStatus:String,val itemUrl:String)

/**
 * 开发者信息bean
 * @param coderEmail 邮箱
 * @param coderId 学号或工号
 * @param coderLogo 头像地址
 * @param coderName 姓名
 * @param coderNumber 号码
 * @param coderSchool 学校
 * @param coderType 开发者身份
 * @param coderAlias 昵称
 */
data class UserInfo(val coderAlias:String,val coderLogo:String,val coderNumber:String,val coderEmail:String,val coderName:String,val coderSchool:String,val coderId:String,val coderType:String)

/**
 * wikiWeb文档bean
 * @param wikiDescribe 接口简介
 * @param wikiInterfaceName 接口调用名字
 * @param wikiName 接口名字
 * @param wikiUrl 接口详情页
 */
data class WikiWebApi(val wikiName:String,val wikiInterfaceName:String,val wikiDescribe:String,val wikiUrl:String)

/**
 * wikiMobile文档bean
 * @param wikiDescribe 接口简介
 * @param wikiName 接口名字
 * @param wikiUrl 接口详情页
 */

data class WikiMobileApi(val wikiName: String,val wikiDescribe: String,val wikiUrl: String)
