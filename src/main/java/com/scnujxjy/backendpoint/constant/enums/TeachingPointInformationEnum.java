package com.scnujxjy.backendpoint.constant.enums;

import lombok.Getter;

@Getter
public enum TeachingPointInformationEnum {
    ADDRESS1("广州达德教学点", "广州市"),
    ADDRESS2("广州东方教学点", "广州市"),
    ADDRESS3("广州增城职大教学点", "广州市"),
    ADDRESS4("广州海珠蓝星教学点", "广州市"),
    ADDRESS5("广州番禺学程教学点", "广州市"),
    ADDRESS6("广州天河越平教学点", "广州市"),
    ADDRESS7("广州花都英朗教学点", "广州市"),
    ADDRESS8("深圳燕荣教学点", "深圳市"),
    ADDRESS9("深圳明卓教学点", "深圳市"),
    ADDRESS10("深圳华信教学点", "深圳市"),
    ADDRESS11("深圳宝安职训教学点", "深圳市"),
    ADDRESS12("深圳华智教学点", "深圳市"),
    ADDRESS13("深圳龙岗教学点", "深圳市"),
    ADDRESS14("佛山华泰教学点", "佛山市"),
    ADDRESS15("佛山七天教学点", "佛山市"),
    ADDRESS16("佛山三水教学点", "佛山市"),
    ADDRESS17("顺德李伟强职校教学点", "佛山市"),
    ADDRESS18("东莞师华教学点", "东莞市"),
    ADDRESS19("东莞欧龙教学点", "东莞市"),
    ADDRESS20("东莞南方教学点", "东莞市"),
    ADDRESS21("东莞宏达职校教学点", "东莞市"),

    ADDRESS22("惠州孚澳教育教学点", "惠州市"),
    ADDRESS23("中山公众教学点", "中山市"),
    ADDRESS24("珠海博实教学点", "珠海市"),
    ADDRESS25("珠海东剑教学点", "珠海市"),
    ADDRESS26("河源职院教学点", "河源市"),

    ADDRESS27("茂名青年教学点", "茂名市"),
    ADDRESS28("湛江蓝海教学点", "湛江市"),
    ADDRESS29("湛江纺织职校教学点", "湛江市"),
    ADDRESS30("吴川职校教学点", "湛江市"),
    ADDRESS31("阳江开大教学点", "阳江市"),
    ADDRESS32("梅州启航教学点", "梅州市"),
    ADDRESS33("梅州文峰教学点", "梅州市"),
    ADDRESS34("汕头龙湖教学点", "汕头市"),
    ADDRESS35("汕头潮阳教学点", "汕头市"),

    ADDRESS36("韶关大众教学点", "韶关市"),
    ADDRESS37("怀集育才教学点", "肇庆市"),
    ADDRESS38("云浮云城教学点", "云浮市"),
    ADDRESS39("陆丰技校教学点", "汕尾市"),
    ADDRESS40("佛冈县职校教学点", "清远市"),
    ADDRESS41("英德职校教学点", "清远市"),
    ADDRESS42("鹤山开大教学点", "江门市"),
    ADDRESS43("台山开大教学点", "江门市");
//    ADDRESS44("梅州文峰教学点", "梅州市"),
//    ADDRESS45("汕头龙湖教学点", "汕头市"),
//    ADDRESS46("汕头潮阳教学点", "汕头市");

    private String teachingPointName;

    private String address;

    TeachingPointInformationEnum(String teachingPointName, String address) {
        this.teachingPointName = teachingPointName;
        this.address = address;
    }

    public static String getAddressByTeachingPointName(String teachingPointName) {
        for (TeachingPointInformationEnum info : TeachingPointInformationEnum.values()) {
            if (info.teachingPointName.equals(teachingPointName)) {
                return info.address;
            }
        }
        return "";
    }
}
