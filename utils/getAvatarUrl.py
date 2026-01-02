import csv
import requests
import time

# 配置信息
API_KEY = 'YOUR_API_KEY'	# TMDB的API_KEY
BASE_IMAGE_URL = 'https://image.tmdb.org/t/p/w500' # TMDB 图片基础路径
SEARCH_URL = 'https://api.themoviedb.org/3/search/person'

# 读取待处理的 csv 文件 (格式: actor_id, actor_name)
bad_actors = []
with open('actors_avatar_url.csv', 'r', encoding='utf-8') as f:
    reader = csv.reader(f)
    for row in reader:
        bad_actors.append(row)

update_sqls = []

print("开始搜索演员图片...")

for actor_id, actor_name in bad_actors:
    try:
        # 1. 发起搜索请求
        params = {
            'api_key': API_KEY,
            'query': actor_name,
            'language': 'zh-CN' # 尽量匹配中文
        }
        response = requests.get(SEARCH_URL, params=params)
        data = response.json()

        # 2. 解析结果
        if data['results']:
            # 取第一个匹配的结果
            first_match = data['results'][0]
            if first_match.get('profile_path'):
                real_url = BASE_IMAGE_URL + first_match['profile_path']
                
                # 3. 生成 SQL 语句
                sql = f"UPDATE actors SET actor_avatar_url = '{real_url}', update_time = NOW() WHERE actor_id = '{actor_id}';"
                update_sqls.append(sql)
                print(f"成功: {actor_name} -> {real_url}")
            else:
                print(f"跳过: {actor_name} (TMDB无图片)")
        else:
            print(f"跳过: {actor_name} (未找到演员)")
        
        time.sleep(0.2) # 避免请求过快被封

    except Exception as e:
        print(f"错误: {actor_name} - {str(e)}")

# 4. 将生成的 SQL 写入文件
with open('update_avatars.sql', 'w', encoding='utf-8') as f:
    for sql in update_sqls:
        f.write(sql + '\n')

print("完成！请执行 update_avatars.sql")
