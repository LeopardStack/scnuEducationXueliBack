package com.scnujxjy.backendpoint.exception;

public class DataException extends BusinessException {
    public DataException(Integer code, String message) {
        super(code, message);
    }

    public DataException(String message, Integer code) {
        super(message, code);
    }

    public DataException(String message) {
        super(message);
    }

    public static DataException dataNotFoundError() {
        throw new DataException("数据不存在", 2001);
    }

    public static DataException dataUpdateError() {
        throw new DataException("数据更新失败", 2002);
    }

    public static DataException dataDeleteError() {
        throw new DataException("数据删除失败", 2003);
    }

    public static DataException dataInsertError() {
        throw new DataException("数据插入失败", 2004);
    }

    public static DataException dataMissError() {
        throw new DataException("数据缺失失败", 2005);
    }

}
