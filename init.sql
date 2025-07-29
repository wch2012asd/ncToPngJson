-- 初始化数据库脚本
CREATE SCHEMA IF NOT EXISTS public;

-- 创建文件转换记录表
CREATE TABLE IF NOT EXISTS public.file_conversion_records (
    id SERIAL PRIMARY KEY,
    folder_name VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path TEXT NOT NULL,
    output_path TEXT NOT NULL,
    file_size BIGINT,
    conversion_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'SUCCESS',
    UNIQUE(folder_name, file_name)
);

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_folder_name ON public.file_conversion_records(folder_name);
CREATE INDEX IF NOT EXISTS idx_file_name ON public.file_conversion_records(file_name);
CREATE INDEX IF NOT EXISTS idx_conversion_time ON public.file_conversion_records(conversion_time);

-- 插入一些示例数据（可选）
-- INSERT INTO public.file_conversion_records (folder_name, file_name, file_path, output_path, file_size, status)
-- VALUES ('test', 'sample.nc', '/app/input/test/sample.nc', '/app/output/test', 1024, 'SUCCESS');