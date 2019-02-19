%One unit on x abciss is 1 min.

offset = 0;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%    UNIVARIATE TEST ON 10 WEEK FOR AN HEBDOMARY HABIT         %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% period = [10080];
% variance = 0;
% noise = 0;
% reliability1 = [1 0.9 0.8 0.7 0.6 0.5 0.4 0.3 0.2 0.1 ];
% 
% test1_success = zeros(1,10);
% for i=1 : 10
%     for j=1 : 100
%         [finded_period,~] = scenario(period,reliability1(i),variance,noise,10,offset);
%         if finded_period(1) == 10080
%             test1_success(i) = test1_success(i) + 1;
%         end
%     end
% end
% 
% test1_success
%         
% %Study noise impact, 10 week ofhebdomary habbit:
% period = [10080];
% variance = 0;
% noise2 = [0 3 6 10 15 25 45 70 95 140];
% reliability = 1;
% 
% test2_success = zeros(1,10);
% for i=1 : 10
%     for j=1 : 100
%         [finded_period,~] = scenario(period,reliability,variance,noise2(i),10,offset);
%         if finded_period(1) == 10080
%             test2_success(i) = test2_success(i) + 1;
%         end
%     end
% end
% 
% test2_success
% 
% %study impact of variance: from 5 to 5* 2^9
% 
% period = [10080];
% variance3 = [5 10 20 40 80 160 320 640 1280 2560];
% noise = 0;
% reliability = 1;
% 
% test3_success = zeros(1,10);
% for i=1 : 10
%     for j=1 : 100
%         [finded_period,~] = scenario(period,reliability,variance3(i),noise,10,offset);
%         if finded_period(1) == 10080
%             test3_success(i) = test3_success(i) + 1;
%         end
%     end
% end
% 
% test3_success



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%       UNIVARIATE TEST FOR A WEEKLY HABIT ON 3 WEEKS              %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

period = [10080];
variance = 0;
noise = 0;
reliability1 = [1 0.9 0.8 0.7 0.6 0.5 0.4 0.3 0.2 0.1 ];

test4_success = zeros(1,10);
for i=1 : 10
    for j=1 : 100
        [finded_period,~] = scenario(period,reliability1(i),variance,noise,3,offset);
        if finded_period(1) == 10080
            test4_success(i) = test4_success(i) + 1;
        end
    end
end

test4_success
        
%Study noise impact, 10 week ofhebdomary habbit:
period = [10080];
variance = 0;
noise2 = [0 3 6 10 15 25 45 70 95 140];
reliability = 1;

test5_success = zeros(1,10);
for i=1 : 10
    for j=1 : 100
        [finded_period,~] = scenario(period,reliability,variance,noise2(i),3,offset);
        if finded_period(1) == 10080
            test5_success(i) = test5_success(i) + 1;
        end
    end
end

test5_success

%study impact of variance: from 5 to 5* 2^9

period = [10080];
variance3 = [5 10 20 40 80 160 320 640 1280 2560];
noise = 0;
reliability = 1;

test6_success = zeros(1,10);
for i=1 : 10
    for j=1 : 100
        [finded_period,~] = scenario(period,reliability,variance3(i),noise,3,offset);
        if finded_period(1) == 10080
            test6_success(i) = test6_success(i) + 1;
        end
    end
end

test6_success

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                     %   END OF UNIVARIATE TEST    %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%





