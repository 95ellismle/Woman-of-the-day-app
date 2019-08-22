#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Aug 21 12:49:46 2019

@author: oem
"""
import pandas as pd
import time

import myHttp

print("Adding the inspiration Women from googling lists of them")

df = pd.read_csv("./metadata/Women_and_Links.csv")

def get_wiki_page(name):
    """
    Will search ecosia for the wiki page
    """
    time.sleep(0.5)
    search_str = "https://www.ecosia.org/search?q=%s+wiki" % '+'.join(name.split(' '))
    search_page = myHttp.GetPage(search_str, must_contain=[name])
    if not search_page:
        print("Couldn't find link for %s. Ecosia Page Dodgy." % name)
        return False
    soup = search_page.soup
    for a  in soup.find_all("a"):
        if a is None: continue
        attrs = a.attrs
        if attrs is None: continue
        href = attrs.get('href')
        if href is None: continue
        if 'https://en.wikipedia' in href and name.split(" ")[0] in href:
            break
    else:
        print("Couldn't find link for %s" % name)
        return False
    return href


with open('./metadata/Inspirational_Women.list', 'r') as f:  
    txt = f.read().split("\n")  
    txt = [i for i in txt if i]  
    txt = list(set(txt))  

with open("./metadata/Inspirational_Women.list", 'w') as f:  
    f.write('\n'.join(txt)) 

Insp_df = pd.read_csv("./metadata/Inspirational_Women_Links.csv")
dictionary = {i: list(Insp_df[i]) for i in Insp_df.columns}
extra_links = {name: link for name, link in zip(dictionary['names'], dictionary['links'])}

count = 0
missing_women = []
check_names = [i.replace(" ", "") for i in df['names']] 
for name in txt: 
    check = name.replace('’', "'").replace(" ","")
    if check not in check_names: 
        missing_women.append(name) 
        count += 1 
    else:
        for i, check_name in enumerate(check_names):
            if check in check_name:
                link = df.iloc[i]['links']
                true_name = df.iloc[i]['names']
                extra_links[true_name] = link

for name in missing_women:
    if name not in extra_links:
        extra_links[name] = get_wiki_page(name)


def remove_key(D, key):
    if D.get(key) is not None:
        D.pop(key)


remove_key(extra_links,"Dame Judi Dench")
extra_links['Judi Dench'] = 'https://en.wikipedia.org/wiki/Judi_Dench'

remove_key(extra_links,"Dr. Mae Jemison")
extra_links['Mae Jemison'] = 'https://en.wikipedia.org/wiki/Mae_Jemison'

remove_key(extra_links,"Dr Ingrid Mattson")
extra_links['Ingrid Mattson'] = 'https://en.wikipedia.org/wiki/Ingrid_Mattson'

remove_key(extra_links,"Anais Nin")
extra_links['Anaïs Nin'] = 'https://en.wikipedia.org/wiki/Ana%C3%AFs_Nin'

remove_key(extra_links,"Sinead O'Connor")
extra_links['Sinéad O\'Connor'] = 'https://en.wikipedia.org/wiki/Sin%C3%A9ad_O%27Connor'
           
remove_key(extra_links,"Senator Kirsten Gillibrand")
extra_links['Kirsten Gillibrand'] = 'https://en.wikipedia.org/wiki/Kirsten_Gillibrand'
           
remove_key(extra_links,"Graca Machel")
extra_links['Graça Machel'] = 'https://en.wikipedia.org/wiki/Gra%C3%A7a_Machel'
           
remove_key(extra_links,'Lil’ Kim')
extra_links['Lil\' Kim'] = 'https://en.wikipedia.org/wiki/Lil%27_Kim'
           
remove_key(extra_links,'Wangaari Mathaai')
extra_links['Wangari Maathai'] = 'https://en.wikipedia.org/wiki/Wangari_Maathai'
           
remove_key(extra_links,'Brene Brown')
extra_links['Brené Brown'] = 'https://en.wikipedia.org/wiki/Bren%C3%A9_Brown'

extra_links['Estée Lauder'] = 'https://en.wikipedia.org/wiki/Est%C3%A9e_Lauder_(businesswoman)'

remove_key(extra_links, 'bell hooks')
extra_links['Bell Hooks'] = 'https://en.wikipedia.org/wiki/Bell_hooks'


Insp_df = {'names':[], 'links':[], 'category':[]}
for i in extra_links:
    Insp_df['names'].append(i)
    Insp_df['links'].append(extra_links[i])
    Insp_df['category'].append('Unkown')

Insp_df = pd.DataFrame(Insp_df)
Insp_df.to_csv("./metadata/Inspirational_Women_Links.csv", index=False)

extra_women_df = pd.read_csv("./metadata/Extra_Women.csv")
extra_women_df = extra_women_df.append(Insp_df)
extra_women_df = extra_women_df.drop_duplicates('links')
extra_women_df.index = range(len(extra_women_df))
mask = []
all_big_links = list(df['links'])
for i in range(len(extra_women_df)):
    df_entry = extra_women_df.iloc[i]
    link = df_entry['links']
    if link in all_big_links:
        mask.append(False)
    else:
        mask.append(True)

extra_women_df = extra_women_df[mask]
extra_women_df = extra_women_df[extra_women_df['links'] != 'False']
extra_women_df.to_csv("./metadata/Extra_Women.csv", index=False)


