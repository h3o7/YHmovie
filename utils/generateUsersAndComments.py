import pymysql
import uuid
import random
import datetime
from faker import Faker

# ================= 配置区域 =================
# 数据库连接配置 (请修改为你自己的)
DB_CONFIG = {
    "host": "127.0.0.1",
    "port": YOUR_PORT,	# 若是本地，可删除
    "user": "root",       # 你的数据库用户名
    "password": "YOUR_PASSWORD", # 你的数据库密码
    "database": "YOUR_DB", # 你的数据库名
    "charset": "utf8mb4"
}

# 头像 API 服务 (使用 DiceBear 生成卡通头像)
AVATAR_BASE_URL = "https://api.dicebear.com/7.x/adventurer/svg?seed="

# 评论模板 (让评论看起来更真实一点)
COMMENT_TEMPLATES = [
    "这部电影真是太棒了！剧情紧凑，全程无尿点。",
    "演员演技在线，但是剧本稍微有点弱。",
    "特效炸裂，尤其是最后一场戏，值得二刷！",
    "没看懂结局，有人能解释一下吗？",
    "也就那样吧，没有想象中那么好。",
    "这是我今年看过最好的电影，强烈推荐！",
    "背景音乐满分，氛围感拉满。",
    "前面的铺垫太长了，有点想睡觉，后面还行。",
    "反派角色的塑造比主角还要出彩。",
    "不仅是视觉盛宴，更引人深思。",
    "一般般，感觉像是在圈钱。",
    "笑点很密集，全场都在笑。",
    "哭死我了，太感人了，一定要带纸巾。",
    "逻辑硬伤太多，编剧是认真的吗？"
]

REPLY_TEMPLATES = [
    "我也这么觉得！",
    "这就去二刷！",
    "确实，结局我也没看懂。",
    "同感，反派确实很帅。",
    "不至于吧，我觉得挺好看的啊。",
    "楼主正解。",
    "别尬黑，这电影明明很有深度。",
    "哈哈哈哈，真实！"
]

# ================= 工具函数 =================
fake = Faker("zh_CN") # 生成中文数据

def get_uuid():
    return str(uuid.uuid4())

def get_now():
    return datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")

def get_random_time_near_now():
    # 生成最近3个月内的随机时间
    days = random.randint(0, 90)
    seconds = random.randint(0, 86400)
    dt = datetime.datetime.now() - datetime.timedelta(days=days, seconds=seconds)
    return dt.strftime("%Y-%m-%d %H:%M:%S")

def generate_complex_password():
    # 生成8-12位复杂密码 (包含字母和数字)
    chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$"
    length = random.randint(8, 12)
    return "".join(random.choice(chars) for _ in range(length))

def escape_sql(text):
    if not text: return ""
    return str(text).replace("'", "''").replace("\\", "\\\\")

# ================= 主逻辑 =================

try:
    # 1. 连接数据库获取现有电影ID
    print("正在连接数据库读取电影列表...")
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    
    # 按照你的要求查询电影ID
    cursor.execute("SELECT movie_id, movie_name FROM movies")
    movies = cursor.fetchall() # 结果是 ((id, name), (id, name)...)
    
    if not movies:
        print("错误：数据库中没有找到电影数据，请先添加电影。")
        exit()
        
    print(f"读取成功，共有 {len(movies)} 部电影。")

    # 2. 准备 SQL 文件
    f = open("insert_users_comments.sql", "w", encoding="utf-8")
    
    # ----------------------------------------
    # 生成 23 个新用户
    # ----------------------------------------
    print("正在生成 23 个新用户...")
    user_ids = [] # 存储生成的(user_id, user_name)
    
    f.write("-- ================= 新增用户数据 =================\n")
    for i in range(23):
        u_id = get_uuid()
        u_name = fake.user_name() + str(random.randint(100, 999)) # 防止重名
        u_pass = generate_complex_password()
        u_phone = fake.phone_number()
        u_sign = fake.sentence()
        u_gender = random.choice(['M', 'F'])
        u_birth = fake.date_of_birth(minimum_age=18, maximum_age=50)
        u_avatar = f"{AVATAR_BASE_URL}{u_name}" # 使用API生成卡通头像
        now = get_now()
        
        sql = f"""INSERT INTO users (user_id, user_password, user_name, user_signature, user_phone, user_gender, user_birth_date, user_avatar_url, user_last_login_time, user_status, create_time, update_time) VALUES ('{u_id}', '{u_pass}', '{u_name}', '{u_sign}', '{u_phone}', '{u_gender}', '{u_birth}', '{u_avatar}', '{now}', 1, '{now}', '{now}');"""
        
        f.write(sql + "\n")
        user_ids.append(u_id)

    # ----------------------------------------
    # 生成评论 (每部电影 10-15 条)
    # ----------------------------------------
    print("正在生成评论数据...")
    f.write("\n-- ================= 新增评论及回复数据 =================\n")
    
    for movie_id, movie_name in movies:
        # 随机决定这部电影有多少评论 (10-15)
        total_comments = random.randint(10, 15)
        
        # 为了产生回复，我们需要先存下一部分作为“楼主”
        # 假设 60% 是主评论，40% 是回复
        main_comments_count = int(total_comments * 0.6)
        if main_comments_count < 1: main_comments_count = 1
        
        reply_comments_count = total_comments - main_comments_count
        
        # 临时存储这部电影的主评论ID，以便回复使用 [(comment_id, user_id)]
        current_movie_main_comments = []
        
        # A. 生成主评论
        for _ in range(main_comments_count):
            c_id = get_uuid()
            u_id = random.choice(user_ids) # 随机挑一个用户
            content = random.choice(COMMENT_TEMPLATES) + " " + fake.sentence()
            likes = random.randint(0, 200)
            c_time = get_random_time_near_now()
            
            sql_comm = f"""INSERT INTO comments (comment_id, user_id, movie_id, comment_content, comment_like_number, create_time, update_time) VALUES ('{c_id}', '{u_id}', '{movie_id}', '{content}', {likes}, '{c_time}', '{c_time}');"""
            f.write(sql_comm + "\n")
            
            current_movie_main_comments.append({'c_id': c_id, 'u_id': u_id})
            
        # B. 生成回复评论
        for _ in range(reply_comments_count):
            if not current_movie_main_comments: break
            
            # 1. 确定这条回复是谁发的
            replier_user_id = random.choice(user_ids)
            
            # 2. 确定回复哪条评论 (父评论)
            parent_comment = random.choice(current_movie_main_comments)
            parent_c_id = parent_comment['c_id']
            replied_user_id = parent_comment['u_id'] # 被回复的人
            
            # 3. 创建这条回复本身 (它也是一条 comment)
            reply_c_id = get_uuid()
            content = random.choice(REPLY_TEMPLATES) # 模拟@效果
            likes = random.randint(0, 50)
            c_time = get_random_time_near_now()
            
            sql_comm_reply = f"""INSERT INTO comments (comment_id, user_id, movie_id, comment_content, comment_like_number, create_time, update_time) VALUES ('{reply_c_id}', '{replier_user_id}', '{movie_id}', '{content}', {likes}, '{c_time}', '{c_time}');"""
            f.write(sql_comm_reply + "\n")
            
            # 4. 建立回复关系 (插入 comment_replies 表)
            cr_id = get_uuid()
            sql_relation = f"""INSERT INTO comment_replies (comment_reply_id, first_comment_id, second_comment_id, replied_user_id) VALUES ('{cr_id}', '{parent_c_id}', '{reply_c_id}', '{replied_user_id}');"""
            f.write(sql_relation + "\n")

    print(f"生成完毕！已生成文件: insert_users_comments.sql")

except Exception as e:
    print(f"发生错误: {e}")
finally:
    if 'conn' in locals() and conn.open:
        conn.close()
    if 'f' in locals():
        f.close()
