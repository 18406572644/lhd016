-- 初始化补给点数据
INSERT INTO supply_point (name, address, type, status, area, contact_person, contact_phone, longitude, latitude) VALUES
('滨江公园补给站', '滨江区滨江公园东门', '综合补给', '正常', '滨江区', '张三', '13800138001', 120.1551, 30.2741),
('西湖休息站', '西湖区北山街88号', '休息站', '正常', '西湖区', '李四', '13800138002', 120.1562, 30.2672),
('科技园区充电站', '余杭区文一西路969号', '充电站', '正常', '余杭区', '王五', '13800138003', 120.0211, 30.2780),
('运河饮水点', '拱墅区运河广场', '饮水点', '维护中', '拱墅区', '赵六', '13800138004', 120.1456, 30.3210)
ON CONFLICT (id) DO NOTHING;

-- 初始化维修点数据
INSERT INTO repair_shop (name, address, level, area, service_scope, contact_person, contact_phone, staff_count, business_hours) VALUES
('西湖一级维修中心', '西湖区体育场路150号', '一级', '西湖区', '整车维修、配件更换、保养服务', '刘师傅', '13900139001', 8, '08:00-20:00'),
('滨江二级维修站', '滨江区江南大道100号', '二级', '滨江区', '普通维修、充气、补胎', '陈师傅', '13900139002', 4, '09:00-18:00'),
('余杭三级维修点', '余杭区余杭塘路200号', '三级', '余杭区', '简单维修、应急处理', '周师傅', '13900139003', 2, '10:00-17:00')
ON CONFLICT (id) DO NOTHING;

-- 初始化配件数据
INSERT INTO spare_part (part_code, part_name, category, unit, stock_quantity, warning_threshold, unit_price, supplier, supply_point_id) VALUES
('P001', '内胎', '轮胎类', '条', 5, 10, 35.00, '永久配件', 1),
('P002', '外胎', '轮胎类', '条', 12, 8, 85.00, '永久配件', 1),
('P003', '刹车皮', '制动系统', '对', 20, 10, 25.00, '捷安特配件', 1),
('P004', '刹车线', '制动系统', '根', 8, 15, 15.00, '捷安特配件', 2),
('P005', '链条', '传动系统', '条', 3, 5, 65.00, '禧玛诺', 1),
('P006', '脚踏', '传动系统', '副', 18, 10, 45.00, '禧玛诺', 2),
('P007', '矿泉水', '补给品', '瓶', 200, 50, 2.00, '农夫山泉', 1),
('P008', '能量棒', '补给品', '个', 15, 30, 8.00, '康比特', 1)
ON CONFLICT (part_code) DO NOTHING;

-- 初始化求助数据
INSERT INTO help_request (requester_name, requester_phone, location, help_type, urgency, status, description) VALUES
('小明', '13700137001', '滨江区江南大道附近', '车辆故障', '高', 'pending', '自行车链条断裂，无法继续骑行'),
('小红', '13700137002', '西湖景区苏堤', '身体不适', '中', 'processing', '骑行中感觉头晕，需要休息和饮水'),
('小刚', '13700137003', '余杭区未来科技城', '物资需求', '低', 'completed', '需要补充饮用水和能量补给')
ON CONFLICT (id) DO NOTHING;

-- 初始化盘点数据
INSERT INTO inventory_check (check_no, supply_point_id, supply_point_name, check_date, checker, status, total_items, diff_count, remark) VALUES
('PD20250101001', 1, '滨江公园补给站', '2025-01-01', '管理员', 'completed', 5, 1, '年末盘点')
ON CONFLICT (check_no) DO NOTHING;

-- 初始化盘点明细数据
INSERT INTO check_detail (check_id, part_id, part_name, system_quantity, actual_quantity, diff_quantity) VALUES
(1, 1, '内胎', 5, 4, -1),
(1, 2, '外胎', 12, 12, 0),
(1, 3, '刹车皮', 20, 20, 0),
(1, 5, '链条', 3, 3, 0),
(1, 7, '矿泉水', 200, 200, 0)
ON CONFLICT (id) DO NOTHING;
