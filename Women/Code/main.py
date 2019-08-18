 #!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Jul 29 19:37:49 2019

@author: oem
"""
import pandas as pd
import re
import os

import myHttp
import rootPage
import getWomenLinks as womLink

init = True


wiki_url = "https://en.wikipedia.org"
root_url = "%s/wiki/Lists_of_women" % wiki_url





def person_is_male_or_female(filename):
    """
    Will determine from the html file whether the person in question is likely
    female or male.
    """
    with open(filename, 'r') as f:
        txt = f.read().lower().split(' ')
        her_count = txt.count("her")
        his_count = txt.count("his")
        she_count = txt.count("she")
        he_count = txt.count("he")
        
        if he_count == 0 and she_count > 1:
            return 'female'
        elif she_count == 0 and he_count > 1:
            return 'male'
        elif he_count <= 1 and she_count <= 1:
            return False
        
        if she_count / he_count > 3:
            return 'female'
        elif he_count / she_count > 3:
            return 'male'
        else:
            return False
        
        if his_count == 0 and her_count > 0:
            return 'female'
        elif her_count == 0 and his_count > 0:
            return 'male'
        elif his_count == 0 and her_count == 0:
            return False
        
        if her_count / his_count > 3:
            return 'female'
        elif his_count / her_count > 3:
            return 'male'
        else:
            return False


if init:
def tidy_links(link): 
    if link.count("https://") > 1: 
        return False 
    if 'en.wikipedia' not in link: 
        return False 
    else: 
        return link 

    def tidy_name(name):
        """
        Will remove extraneous info from the name such as '(singer)' and generally
        tidy it.
        """
        name = re.sub("\(.*\)", "", name)
        name = name.replace("_", " ")
        return name

    root_soup = myHttp.get_page_soup(root_url, "HTML/root.html")
    
    print("Getting list links")
    all_root_links = rootPage.get_all_links(root_soup, wiki_url,
                                   "./metadata/all_root_links.npy",
                                   "./metadata/all_bad_root_links.npy")
    print("Loading and/or saving pages")
    all_list_soups = rootPage.load_save_all_pages("./HTML/List_Pages/",
                                                  all_root_links,
                                                  wiki_url)
    


    print("Getting links for women's pages.")        
    df = womLink.get_all_women_links(all_list_soups, wiki_url)

    extra_list = ['https://en.wikipedia.org/wiki/Category:American_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:Australian_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:Belgian_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:Brazilian_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:British_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:Canadian_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:English_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:Filipino_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:French_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:German_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:Indian_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:Iranian_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:Japanese_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:Lebanese_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:Malaysian_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:Mexican_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:New_Zealand_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:Nigerian_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:South_Korean_female_pop_singers',
                  'https://en.wikipedia.org/wiki/Category:Spanish_female_pop_singers']
    all_links = []
    all_categories = []
    for url in extra_list:
        name = url[url.rfind('Category:'):]
        filename = "/home/oem/AndroidStudioProjects/WomanoftheDay/Women/Code/HTML/List_Pages/%s" % (name)
        soup = myHttp.get_page_soup(url, filename)
        links = rootPage.get_all_links(soup, wiki_url)
        for link in links:
            all_categories.append(name)
            all_links.append(link)
    df_extra = {'names': [], 'links':[], 'category':[]}
    for link, cat in zip(all_links, all_categories):
        name = link[link.rfind('/')+1:]
        df_extra['names'].append(name)
        df_extra['links'].append(link)
        df_extra['category'].append(cat)
    df_extra = pd.DataFrame(df_extra)
    
    df_manual = pd.read_csv("./metadata/Extra_Women.csv")
    df = df.append(df_extra)
    df = df.append(df_manual)
    df.index = range(len(df))
    df.drop_duplicates('links')
    df['names'] = df['names'].apply(tidy_name)
    df['links'] = df['links'].apply(tidy_links) 
    df = df[df['links'] != False] 
    

    bad_names = []
    for name in df['names']:
        if any(j in name for j in (':', ';', '\'s')):
            bad_names.append(name)
        
        good_nouns = womLink.check_for_nouns(name, 1)
        if not good_nouns:
            bad_words = [word for word in name.split(' ') if word in ('and', 'for')]
            if len(bad_words):
                bad_names.append(name)
    
    for name in bad_names:
        df = df[df['names'] != name]
    
    all_links = [] 
    for fN in os.listdir('./HTML/Women'): 
        gender = person_is_male_or_female(fN) 
        if gender == 'male': 
            all_links.append(fN.replace(".html", "")) 
        
    for bad_name in all_links:  
        df = df[~df['links'].str.contains(bad_name)] 
    
    df.to_csv("./metadata/Women_and_Links.csv", index=False)


df = pd.read_csv("./metadata/Women_and_Links.csv")
all_links = rootPage.load_save_all_pages("./HTML/Women/", list(df['links']),
                                         wiki_url, do_soup=False)



# Need to find when to use table, list and text       X
# Need to download all the pages                              
# Need to create a way to determine whether the page is big enough/has enough info
# Need to find a way to determine whether the page is definitely about a woman (uses lots of she rather than he?)
# Need to create a way to strip useful data
# Need to whack it into a database
