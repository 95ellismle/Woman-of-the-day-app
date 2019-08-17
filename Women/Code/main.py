 #!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Jul 29 19:37:49 2019

@author: oem
"""
import pandas as pd

import myHttp
import rootPage
import getWomenLinks as womLink

init = False


wiki_url = "https://en.wikipedia.org"
root_url = "%s/wiki/Lists_of_women" % wiki_url


if init:
    root_soup = myHttp.get_page_soup(root_url, "HTML/root.html")
    
    print("Getting list links")
    all_root_links = rootPage.get_all_links(root_soup, wiki_url,
                                   "./metadata/all_root_links.npy",
                                   "./metadata/all_bad_root_links.npy")
    print("Loading and/or saving pages")
    all_list_soups = rootPage.load_save_all_pages("HTML/List_Pages/",
                                                  all_root_links)
    
    print("Getting links for women's pages.")
    df = womLink.get_all_women_links(all_list_soups, wiki_url)

df = pd.read_csv("./metadata/Women_and_Links.csv")
all_links = rootPage.load_save_all_pages("./HTML/Women/", list(df['links']),
                                         do_soup=False)


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
        
        if he_count == 0 and she_count > 0:
            return 'female'
        elif she_count == 0 and he_count > 0:
            return 'male'
        elif he_count == 0 and she_count == 0:
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


# Need to find when to use table, list and text       X
# Need to download all the pages                              
# Need to create a way to determine whether the page is big enough/has enough info
# Need to find a way to determine whether the page is definitely about a woman (uses lots of she rather than he?)
# Need to create a way to strip useful data
# Need to whack it into a database
