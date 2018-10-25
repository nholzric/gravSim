% filePath = '.\gravScenario.txt';
function sim = loadSim(filePath)
    fid = fopen(filePath,'r');
    fileText = textscan(fid,'%s','Delimiter','\n'); fileText = fileText{1};
    fclose(fid);
    
    sim.name = '';
    sim.p = [];
    sim.v = [];
    sim.gm = [];
    sim.um = [];
    dataIndex = 0;
    for i = 1:size(fileText,1)
        if isempty(fileText{i}); continue; end
        
        lineText = textscan(fileText{i},'%s','Delimiter','\t'); lineText = lineText{1};
        
        tag = lineText{1};
        if strcmp(tag,'NAME')
            assert(size(lineText,1)>=2,'Empty Name Tag');
            
            dataIndex = dataIndex + 1;

            if isempty(sim.name)
                sim.name = str2double(lineText{2});
                sim.p = nan(1,2);
                sim.v = nan(1,2);
                sim.gm = nan;
                sim.um = nan;
            else
                sim.name = [sim.name; str2double(lineText{2})];
                sim.p = [sim.p; nan(1,2)];
                sim.v = [sim.v; nan(1,2)];
                sim.gm = [sim.gm; nan];
                sim.um = [sim.um; nan];
            end
        else
            if dataIndex == 0; continue; end %require a NAME to come first
            switch tag
                case 'POSITION'
                    assert(size(lineText,1)>=3,'Not Enought Position Dimensions');
                    sim.p(dataIndex,:) = [str2double(lineText{2}),str2double(lineText{3})];
                    
                case 'VELOCITY'
                    assert(size(lineText,1)>=3,'Not Enought Velocity Dimensions');
                    sim.v(dataIndex,:) = [str2double(lineText{2}),str2double(lineText{3})];
                    
                case 'GRAVITYMASS'
                    assert(size(lineText,1)>=2,'Empty Gravity Tag');
                    sim.gm(dataIndex,1) = str2double(lineText{2});
                    
                case 'MYTHIUMMASS'
                    assert(size(lineText,1)>=2,'Empty Mythium Tag');
                    sim.gm(dataIndex,1) = str2double(lineText{2});
            end
        end
    end
end