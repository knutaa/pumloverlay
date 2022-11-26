#!/bin/python
# generate diff with differences in API versions

import sys
import os
import argparse
from os import path
import subprocess
import tempfile
import shutil
import yaml
from yaml import Loader
import json

bin_path = os.path.dirname(os.path.realpath(__file__))

APIDIAGRAM=path.join(bin_path,"apidiagram-3.2.0-SNAPSHOT.jar")
OVERLAY=path.join(bin_path,"pumloverlay-2.2.0-SNAPSHOT.jar")

#
# Create the configuration file directing the diagram generation for diffs
# 
def create_config(dir,file):
   expand = """
   {
      "removeInheritedAllOfEdges": true,
      "coreInheritanceRegexp": [
      ".*"
      ],
      "keepInheritanceDecoractions": true,
      "inheritedFormatting": "%s",
      "resourceMapping": {
		   "EventSubscription": "Hub"
	   }
   }         
   """

   expand_json=path.join(dir,file)
   f = open(expand_json,"w+")
   f.writelines(expand)
   
   return expand_json

def check_file_exists(*args):
   for file in args:
      if not path.isfile(file):
         print(f"ERROR: file not found: {file}")
         sys.exit() 

#
# utility to check for create directory if not existing
#
def setup_directory(dir):
   if path.isfile(dir):
      print(f"ERROR: directory {dir} exist and is a file")
      sys.exit()

   if not path.isdir(dir):
      print(f"directory {dir} created")
      try:
         os.makedirs(dir) 
      except Exception as ex:
         print(f"ERROR: unable to create directory {dir} error={ex}")
         sys.exit()

def extract_extensions(base,api,label,color,config,dir,subdir,subconfig=None):
    extract_config=f"config-{label}.json"
    cmd = [
      'java',
      '-jar',
      APIDIAGRAM,
      'extract-extensions',  
      '--base', base,
      '--openapi', api,
      '--extension-label', label,
      '--extension-color', color,
      '--config', config,
      '--target-directory', dir,
      '--output', extract_config
    ]

    res = subprocess.run(cmd)
    if res.returncode != 0:
        print(f"ERROR: unable to extract extensions args={cmd}")
        sys.exit()

    cmd = [
         'java',
         '-jar',
         APIDIAGRAM,
         'diagrams',  
         '--openapi', api,
         '--config', config,
         '--config', path.join(dir, extract_config),
         '--target-directory',  path.join(dir, subdir)
    ]
      
    if subconfig and path.isfile(subconfig):
        cmd.append('--config')
        cmd.append(subconfig)

    res = subprocess.run(cmd)
    if res.returncode != 0:
        print(f"ERROR: unable to genenerate diagrams args={cmd}")
        sys.exit()

#
# Combine puml files
#
def generate_overlay(base,file1,file2,target):
   cmd = [
      'java',
      '-jar',
      OVERLAY,
      'overlay',  
      '--file', file1,
      '--file', file2,
      '--output',  path.join(target, base)
   ]

   res = subprocess.run(cmd)
   if res.returncode != 0:
      print(f"ERROR: unable to generate diagram diff (overlay) args={cmd}")
      sys.exit()

#
# Generate subresource config to apply to the base diagram layout
#
def create_subresource_config(dir, filename):
    config = {}

    if path.isfile(filename):
        with open(filename, 'r') as file:
            diagrams = yaml.safe_load(file)
            keys=[list(item.keys()) for item in diagrams['graphs'] ]
            keys=[item for sublist in keys for item in sublist if '_' in item]
            res={}
            for key in keys:
                [resource,subresource] = key.split('_')
                if not resource in res:
                    res[resource] = []
                res[resource].append(subresource)

            if res:
                config['subResourceConfig'] = res
                
    subresource_json=path.join(dir,"subresource.json")
    f = open(subresource_json,"w")
    config = json.dumps(config, indent = 4)
    f.writelines(config)
   
    return subresource_json

#
# Perform the overlay given input arguments 
# 
def execute(args):
    check_file_exists(args.prev, args.current)
    setup_directory(args.target_directory)

    with tempfile.TemporaryDirectory() as tmpdir:
        tmpdir_name=str(tmpdir) # tmpdir_name='./tmp' # tmpdir_name=str(tmpdir)

        expand_json=create_config(tmpdir_name,'expand.json')

        extract_extensions(args.prev,args.current,'added','blue',expand_json,tmpdir_name,'v5')
       
        subresource_config=create_subresource_config(tmpdir_name, path.join(tmpdir_name, "v5", "diagrams.yaml"))

        extract_extensions(args.current,args.prev,'removed','red',expand_json,tmpdir_name,'v4', subresource_config)

        for base in [f for f in os.listdir(path.join(tmpdir_name, "v5")) if f.endswith(".puml")]:
            file1=path.join(tmpdir_name, "v4", base)
            file2=path.join(tmpdir_name, "v5", base)
            if path.isfile(file1):
                generate_overlay(base,file1,file2,args.target_directory)
            else:
                shutil.copy(file2, args.target_directory)
    
#
# main 
#
if __name__ == '__main__':
   check_file_exists(APIDIAGRAM, OVERLAY)

   parser = argparse.ArgumentParser(
                     prog = 'gen_diagram_diff',
                     description = 'Generate API diagram diff',
                     epilog = 'Output will be to the --target-directory (default current directory)')

   parser.add_argument('--prev', required=True, help='filename for the previous ("old") version of the API')
   parser.add_argument('--current', required=True, help='filename for the current ("new") version of the API')
   parser.add_argument('--target-directory', default='.', help='output directory generated diagrams')

   args = parser.parse_args()

   execute(args)

