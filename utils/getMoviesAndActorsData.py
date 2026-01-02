import requests
import uuid
import datetime
import random
import json

# ================= 配置区域 =================
# 请去 https://www.themoviedb.org/settings/api 申请一个 API Key
API_KEY = "YOUR_API_KEY"	# TMDB的api_key
LANGUAGE = "zh-CN"  # 简体中文
TARGET_MOVIE_COUNT = 137  # 目标生成电影数量

# ================= 全局缓存 (用于去重) =================
# 格式: { 'tmdb_genre_id': '生成的uuid' }
saved_genres = {}
# 格式: { 'tmdb_person_id': '生成的uuid' }
saved_actors = {}

# SQL 文件句柄
sql_file = open("insert_movies.sql", "w", encoding="utf-8")

def escape_sql(text):
    """处理 SQL 中的单引号转义"""
    if text is None:
        return ""
    return str(text).replace("'", "''").replace("\\", "\\\\")

def get_uuid():
    return str(uuid.uuid4())

def get_now():
    return datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")

def write_sql(sql):
    sql_file.write(sql + ";\n")

# ================= 核心逻辑 =================

def process_movies():
    # 1. 初始化：生成清理旧数据的 SQL
    print("正在生成清理语句...")
    write_sql("DELETE FROM movie_actors")
    write_sql("DELETE FROM movie_types")
    write_sql("DELETE FROM movies")
    write_sql("DELETE FROM actors")
    write_sql("DELETE FROM types")
    
    movies_collected = 0
    page = 1
    
    while movies_collected < TARGET_MOVIE_COUNT:
        print(f"正在获取第 {page} 页电影数据...")
        
        # 获取热门电影列表
        url = f"https://api.themoviedb.org/3/movie/popular?api_key={API_KEY}&language={LANGUAGE}&page={page}"
        response = requests.get(url)
        
        if response.status_code != 200:
            print(f"API 请求失败: {response.text}")
            break
            
        data = response.json()
        results = data.get('results', [])
        
        if not results:
            break
            
        for movie_simple in results:
            if movies_collected >= TARGET_MOVIE_COUNT:
                break
                
            tmdb_id = movie_simple['id']
            
            # 获取电影详情（包含时长、详细类型等）
            detail_url = f"https://api.themoviedb.org/3/movie/{tmdb_id}?api_key={API_KEY}&language={LANGUAGE}"
            detail_res = requests.get(detail_url)
            if detail_res.status_code != 200:
                continue
            movie = detail_res.json()
            
            # ---------------- 1. 处理电影表 (Movies) ----------------
            movie_uuid = get_uuid()
            title = escape_sql(movie.get('title'))
            poster = f"https://image.tmdb.org/t/p/w500{movie.get('poster_path')}" if movie.get('poster_path') else ""
            duration = movie.get('runtime', 0) or 90
            rating = movie.get('vote_average', 0)
            desc = escape_sql(movie.get('overview', ''))
            
            # 国家和语言
            countries = movie.get('production_countries', [])
            country = escape_sql(countries[0]['iso_3166_1']) if countries else "CN"
            lang = escape_sql(movie.get('original_language', 'zh'))
            
            # 票房 (TMDB有时为0，随机生成一个模拟数据如果为0)
            box_office = movie.get('revenue', 0)
            if box_office == 0:
                box_office = random.randint(1000000, 100000000)
                
            release_date = movie.get('release_date', '2023-01-01')
            if not release_date: release_date = '2023-01-01'
            
            # 简单的下映日期逻辑：上映后30天
            release_dt = datetime.datetime.strptime(release_date, "%Y-%m-%d")
            end_date = (release_dt + datetime.timedelta(days=30)).strftime("%Y-%m-%d")
            
            sql_movie = f"""
            INSERT INTO movies (movie_id, movie_name, movie_poster_url, movie_duration, movie_rating, 
                                movie_description, movie_country, movie_language, movie_box_office, 
                                movie_release_date, movie_end_date, create_time, update_time)
            VALUES ('{movie_uuid}', '{title}', '{poster}', {duration}, {rating}, 
                    '{desc}', '{country}', '{lang}', {box_office}, 
                    '{release_date}', '{end_date}', '{get_now()}', '{get_now()}')
            """
            write_sql(sql_movie)
            
            # ---------------- 2. 处理类型及关系 (Types & Movie_Types) ----------------
            genres = movie.get('genres', [])
            for genre in genres:
                g_id = genre['id']
                g_name = escape_sql(genre['name'])
                
                # 检查类型是否已存在，不存在则插入
                if g_id not in saved_genres:
                    type_uuid = get_uuid()
                    saved_genres[g_id] = type_uuid
                    sql_type = f"""
                    INSERT INTO types (type_id, type_name, type_description, create_time, update_time)
                    VALUES ('{type_uuid}', '{g_name}', '电影类型: {g_name}', '{get_now()}', '{get_now()}')
                    """
                    write_sql(sql_type)
                else:
                    type_uuid = saved_genres[g_id]
                
                # 插入电影-类型关系
                sql_mt = f"""
                INSERT INTO movie_types (movie_type_id, movie_id, type_id, create_time, update_time)
                VALUES ('{get_uuid()}', '{movie_uuid}', '{type_uuid}', '{get_now()}', '{get_now()}')
                """
                write_sql(sql_mt)

            # ---------------- 3. 处理演员及关系 (Actors & Movie_Actors) ----------------
            # 获取演职员表
            credits_url = f"https://api.themoviedb.org/3/movie/{tmdb_id}/credits?api_key={API_KEY}&language={LANGUAGE}"
            credits_res = requests.get(credits_url)
            if credits_res.status_code == 200:
                credits_data = credits_res.json()
                
                # A. 处理演员 (取前 8 位)
                cast_list = credits_data.get('cast', [])[:8]
                for person in cast_list:
                    process_person(person, movie_uuid, 'actor')

                # B. 处理导演
                crew_list = credits_data.get('crew', [])
                directors = [p for p in crew_list if p['job'] == 'Director']
                for person in directors:
                    process_person(person, movie_uuid, 'director')

            print(f"完成电影: {title}")
            movies_collected += 1
            
        page += 1

def process_person(person_data, movie_uuid, role_type):
    """处理单个人员（演员或导演）"""
    tmdb_person_id = person_data['id']
    name = escape_sql(person_data['name'])
    
    # 尝试获取头像
    profile_path = person_data.get('profile_path')
    avatar_url = f"https://image.tmdb.org/t/p/w200{profile_path}" if profile_path else ""
    
    # 检查人是否已存在
    if tmdb_person_id not in saved_actors:
        actor_uuid = get_uuid()
        saved_actors[tmdb_person_id] = actor_uuid
        sql_actor = f"""
        INSERT INTO actors (actor_id, actor_name, actor_avatar_url, create_time, update_time)
        VALUES ('{actor_uuid}', '{name}', '{avatar_url}', '{get_now()}', '{get_now()}')
        """
        write_sql(sql_actor)
    else:
        actor_uuid = saved_actors[tmdb_person_id]
    
    # 插入关系
    # 演员有角色名，导演一般没有（或者就是"Director"）
    char_name = escape_sql(person_data.get('character', ''))
    if role_type == 'director':
        char_name = '导演'
        
    # 截断角色名防止超长 (varchar 25)
    if len(char_name) > 25:
        char_name = char_name[:24]
        
    sql_relation = f"""
    INSERT INTO movie_actors (movie_actor_id, movie_id, actor_id, character_name, movie_role_type, create_time, update_time)
    VALUES ('{get_uuid()}', '{movie_uuid}', '{actor_uuid}', '{char_name}', '{role_type}', '{get_now()}', '{get_now()}')
    """
    write_sql(sql_relation)

# ================= 运行 =================
if __name__ == "__main__":
    if "你的_TMDB_API_KEY" in API_KEY:
        print("请先在脚本中配置 API_KEY！")
    else:
        try:
            process_movies()
            print(f"成功！已生成 SQL 文件: insert_movies.sql，包含 {TARGET_MOVIE_COUNT} 部电影的数据。")
        finally:
            sql_file.close()
